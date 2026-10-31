using Lacuna.Signer.Api;
using Lacuna.Signer.Api.Documents;
using Lacuna.Signer.Api.FlowActions;
using Lacuna.Signer.Api.Folders;
using Lacuna.Signer.Api.Invoices;
using Lacuna.Signer.Api.Users;
using Lacuna.Signer.Client;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

namespace Console.Scenarios
{
    public abstract class Scenario
    {
        private static readonly HttpClient HttpClient = new HttpClient();

        protected SignerClient SignerClient;

        // Kept for CallSignerApiAsync.
        private readonly string domain;
        private readonly string token;

        public Scenario()
        {
            // Signer instance URL. Without SIGNER_ENDPOINT, the samples use Lacuna's Signer sandbox.
            domain = GetEnvironmentVariableOrDefault("SIGNER_ENDPOINT", "https://signer-lac.azurewebsites.net");
            // Application credentials token, "<application name>|<key>". Without SIGNER_API_KEY, the
            // samples use a shared key of the sandbox.
            token = GetEnvironmentVariableOrDefault("SIGNER_API_KEY", "API Sample App|43fc0da834e48b4b840fd6e8c37196cf29f919e5daedba0f1a5ec17406c13a99");
            SignerClient = new SignerClient(domain, token);
        }

        // Calls the Signer REST API directly, for the features that Lacuna.Signer.Client and
        // Lacuna.Signer.Api don't have yet, such as pausing a document. The body is sent as JSON, with the
        // field names and enum values of the API reference (https://www.dropsigner.com/swagger). Returns
        // the JSON of the response, or null when it has none. Throws on an answer other than 2xx, with the
        // body, which for a 422 holds Signer's error code and message, and on a 2xx that isn't JSON.
        protected async Task<JToken> CallSignerApiAsync(HttpMethod method, string path, object body = null)
        {
            using (var request = new HttpRequestMessage(method, domain.TrimEnd('/') + path))
            {
                request.Headers.TryAddWithoutValidation("X-Api-Key", token);
                if (body != null)
                {
                    request.Content = new StringContent(JsonConvert.SerializeObject(body), Encoding.UTF8, "application/json");
                }
                using (var response = await HttpClient.SendAsync(request))
                {
                    var content = await response.Content.ReadAsStringAsync();
                    if (!response.IsSuccessStatusCode)
                    {
                        throw new HttpRequestException($"{method} {path} answered {(int)response.StatusCode}: {content}");
                    }
                    if (string.IsNullOrEmpty(content))
                    {
                        return null;
                    }
                    try
                    {
                        return JToken.Parse(content);
                    }
                    catch (JsonReaderException ex)
                    {
                        throw new HttpRequestException($"{method} {path} answered {(int)response.StatusCode} with invalid JSON: {content}", ex);
                    }
                }
            }
        }

        // Returns a field of a response from CallSignerApiAsync, and throws if Signer left it out or sent
        // null: an instance older than the feature ignores the request field instead of rejecting it.
        protected static JToken RequiredField(JToken parent, string name)
        {
            var value = parent[name];
            if (value == null || value.Type == JTokenType.Null)
            {
                throw new InvalidOperationException($"Signer did not return {name}: this instance may predate the feature");
            }
            return value;
        }

        public abstract Task RunAsync();

        // Returns the value of an environment variable, or the fallback when it is unset or empty.
        protected static string GetEnvironmentVariableOrDefault(string name, string fallback)
        {
            var value = Environment.GetEnvironmentVariable(name);
            return string.IsNullOrEmpty(value) ? fallback : value;
        }

        // Creates a generic document, useful for certain scenarios.
        protected async Task<CreateDocumentResult> CreateDocumentAsync()
        {
            var filePath = "sample.pdf";
            var fileName = Path.GetFileName(filePath);
            var file = File.ReadAllBytes(filePath);
            var uploadModel = await SignerClient.UploadFileAsync(fileName, file, "application/pdf");

            var documentModel = new FileUploadModel(uploadModel) { DisplayName = "Check Status Sample" };

            var participant = new ParticipantUserModel()
            {
                Name = "Jack Bauer",
                Email = "jack.bauer@mailinator.com",
                Identifier = "75502846369"
            };

            var flowAction = new FlowActionCreateModel()
            {
                Type = FlowActionType.Signer,
                User = participant
            };

            var documentRequest = new CreateDocumentRequest()
            {
                Files = new List<FileUploadModel>() { documentModel },
                FlowActions = new List<FlowActionCreateModel>() { flowAction }
            };

            return (await SignerClient.CreateDocumentAsync(documentRequest)).First();
        }
    }
}
