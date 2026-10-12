using Lacuna.Signer.Api;
using Lacuna.Signer.Api.Webhooks;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Concurrent;
using System.IO;
using System.Linq;
using System.Net;
using System.Security.Cryptography;
using System.Text;
using System.Threading.Tasks;

namespace Console.Scenarios
{
    /**
     * A minimal webhook endpoint, built on System.Net.HttpListener (no web framework). It listens on
     * http://localhost:8080/webhook and follows the rules of
     * https://docs.lacunasoftware.com/en-us/articles/signer/webhooks#best-practices:
     *
     * 1. Check the auth header registered with the webhook: Signer does not sign the body.
     * 2. Answer 200 at once and process the event afterwards. Signer retries a non-2xx answer or a
     *    connection error (up to 10 times by default), so slow work before answering causes retries.
     * 3. Be idempotent: the same event can arrive more than once.
     * 4. Don't trust the order of events: read the document from Signer before acting on it.
     *
     * Select it in Program.cs and press F5. The README shows how to test it with curl, and how to
     * receive real events through an HTTPS tunnel.
     */
    public class WebhookEndpointScenario : Scenario
    {
        // HttpListener only accepts requests whose Host header matches the prefix, here "localhost".
        private const string ListenerPrefix = "http://localhost:8080/";
        private const string WebhookPath = "/webhook";

        // The token registered with the webhook under Organization > Integration, with the auth type
        // "Bearer": Signer then sends "Authorization: Bearer <token>". Use a long random value of your own.
        private const string ExpectedAuthorization = "Bearer my-webhook-token";

        // Keys of the events already received. A sample can keep them in memory; production needs durable
        // storage, for example a table with a unique index on the key, shared by every instance of the app.
        private readonly ConcurrentDictionary<string, bool> receivedEvents = new ConcurrentDictionary<string, bool>();

        // Its HandleWebhook method has the switch on the event type.
        private readonly CheckDocumentStatusScenario documentEventsHandler = new CheckDocumentStatusScenario();

        public override async Task RunAsync()
        {
            var listener = new HttpListener();
            listener.Prefixes.Add(ListenerPrefix);
            listener.Start();
            System.Console.WriteLine($"Listening on http://localhost:8080{WebhookPath}. Press Ctrl+C to stop.");

            while (true)
            {
                var context = await listener.GetContextAsync();
                _ = Task.Run(() => HandleRequestAsync(context));
            }
        }

        private async Task HandleRequestAsync(HttpListenerContext context)
        {
            var request = context.Request;
            var response = context.Response;
            JObject webhook;
            string key;
            try
            {
                if (request.HttpMethod != "POST" || request.Url.AbsolutePath != WebhookPath)
                {
                    Respond(response, 404);
                    return;
                }

                // 1. Check the auth header before reading the body.
                if (!IsAuthorized(request.Headers["Authorization"]))
                {
                    Respond(response, 401);
                    return;
                }

                // The body is { "type": "...", "data": { ... } }, with camelCase fields. Read it as JSON rather
                // than as a WebhookModel, which rejects the event types that this version of
                // Lacuna.Signer.Api doesn't know (DocumentsCreated, DocumentDeleted).
                string body;
                using (var reader = new StreamReader(request.InputStream, Encoding.UTF8))
                {
                    body = await reader.ReadToEndAsync();
                }
                try
                {
                    webhook = JObject.Parse(body);
                    key = GetEventKey(webhook);
                }
                catch (Exception)
                {
                    // Not JSON, or not shaped like a webhook.
                    Respond(response, 400);
                    return;
                }

                // 3. Answer 200 to an event already received, so that Signer stops sending it, but don't
                //    process it again.
                if (!receivedEvents.TryAdd(key, true))
                {
                    System.Console.WriteLine($"Ignored a repeated event: {key}");
                    Respond(response, 200);
                    return;
                }
            }
            catch (Exception ex)
            {
                System.Console.WriteLine($"Could not read the request: {ex.Message}");
                response.Abort();
                return;
            }

            // 2. Answer now, then process the event. Process it even if the answer fails: the copy that
            //    Signer sends again will be ignored as a repeat.
            try
            {
                Respond(response, 200);
            }
            catch (Exception ex)
            {
                System.Console.WriteLine($"Could not answer {key}: {ex.Message}");
            }
            System.Console.WriteLine($"Accepted: {key}");

            try
            {
                await ProcessEventAsync(webhook);
            }
            catch (Exception ex)
            {
                // The event was already answered, so Signer won't send it again: in production, keep it in
                // your storage and retry it from there.
                System.Console.WriteLine($"Could not process {key}: {ex.Message}");
            }
        }

        private async Task ProcessEventAsync(JObject webhook)
        {
            WebhookTypes type;
            if (!Enum.TryParse((string)webhook["type"], out type))
            {
                System.Console.WriteLine($"This sample does not handle {webhook["type"]} events");
                return;
            }

            if (type == WebhookTypes.DocumentConcluded)
            {
                // 4. Events can arrive out of order, and the payload is a notification, not the source of
                //    truth: read the document and act only if it is really concluded.
                var document = await SignerClient.GetDocumentDetailsAsync((Guid)webhook["data"]["id"]);
                if (document.Status != DocumentStatus.Concluded)
                {
                    System.Console.WriteLine($"Document {document.Id} is {document.Status}, not Concluded: nothing to do yet");
                    return;
                }
            }

            documentEventsHandler.HandleWebhook(new WebhookModel() { Type = type, Data = webhook["data"] });
        }

        private static bool IsAuthorized(string authorization)
        {
            // Compare SHA-256 hashes, so that the comparison time doesn't reveal how much of the token matched.
            using (var sha256 = SHA256.Create())
            {
                var expected = sha256.ComputeHash(Encoding.UTF8.GetBytes(ExpectedAuthorization));
                var actual = sha256.ComputeHash(Encoding.UTF8.GetBytes(authorization ?? ""));
                return actual.SequenceEqual(expected);
            }
        }

        // The key of an event: its type, the document id and, for an event about one participant's action,
        // the flow action id (a document gets one DocumentSigned per signer). Throws if the body has no type.
        private static string GetEventKey(JObject webhook)
        {
            var type = (string)webhook["type"];
            if (string.IsNullOrEmpty(type))
            {
                throw new FormatException("The body has no type");
            }
            var data = webhook["data"] as JObject ?? new JObject();
            // DocumentsCreated and DocumentDeleted carry a list of documents instead of one id.
            var documentId = (string)data["id"]
                ?? string.Join(",", (data["documents"] as JArray ?? new JArray()).Select(document => (string)document.SelectToken("id")));
            var flowActionId = (string)(data.SelectToken("signature.flowActionId")
                ?? data.SelectToken("approval.flowActionId")
                ?? data.SelectToken("refusal.flowActionId"));
            return string.Join(":", new[] { type, documentId, flowActionId }.Where(part => !string.IsNullOrEmpty(part)));
        }

        private static void Respond(HttpListenerResponse response, int statusCode)
        {
            response.StatusCode = statusCode;
            response.Close();
        }
    }
}
