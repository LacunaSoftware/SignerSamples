import * as crypto from "crypto";
import * as http from "http";
import {
  DocumentsApi,
  DocumentStatus,
  WebhooksWebhookModel,
  WebhookTypes,
} from "signer-node-client";
import { config } from "./scenario";
import { handleWebhook } from "./checkDocumentStatusScenario";

/**
 * A minimal webhook endpoint, built on Node's http module (no web framework). It listens on
 * http://localhost:8080/webhook and follows the rules of
 * https://docs.lacunasoftware.com/en-us/articles/signer/webhooks#best-practices:
 *
 * 1. Check the auth header registered with the webhook: Signer does not sign the body.
 * 2. Answer 200 at once and process the event afterwards. Signer retries a non-2xx answer or a
 *    connection error (up to 10 times by default), so slow work before answering causes retries.
 * 3. Be idempotent: the same event can arrive more than once.
 * 4. Don't trust the order of events: read the document from Signer before acting on it.
 *
 * Run it with `npx ts-node scenarios/webhookEndpointScenario.ts`. The README shows how to test it
 * with curl, and how to receive real events through an HTTPS tunnel.
 */

const port = 8080;
const webhookPath = "/webhook";

// The token registered with the webhook under Organization > Integration, with the auth type
// "Bearer": Signer then sends "Authorization: Bearer <token>". Use a long random value of your own.
const expectedAuthorization = "Bearer my-webhook-token";

// Keys of the events already received. A sample can keep them in memory; production needs durable
// storage, for example a table with a unique index on the key, shared by every instance of the app.
const receivedEvents = new Set<string>();

const documentsApi = new DocumentsApi(config);

const server = http.createServer((req, res) => {
  if (req.method !== "POST" || req.url !== webhookPath) {
    res.writeHead(404).end();
    return;
  }

  // 1. Check the auth header before reading the body.
  if (!isAuthorized(req.headers.authorization)) {
    res.writeHead(401).end();
    return;
  }

  const chunks: Buffer[] = [];
  req.on("data", (chunk: Buffer) => chunks.push(chunk));
  req.on("end", () => {
    // The body is { "type": "...", "data": { ... } }, with camelCase fields.
    const webhook = parseWebhook(Buffer.concat(chunks).toString("utf8"));
    if (!webhook) {
      res.writeHead(400).end();
      return;
    }

    // 3. Answer 200 to an event already received, so that Signer stops sending it, but don't
    //    process it again.
    const key = getEventKey(webhook);
    if (receivedEvents.has(key)) {
      console.log(`Ignored a repeated event: ${key}`);
      res.writeHead(200).end();
      return;
    }
    receivedEvents.add(key);

    // 2. Answer now, then process the event.
    res.writeHead(200).end();
    console.log(`Accepted: ${key}`);
    processEvent(webhook).catch((err) =>
      // The event was already answered, so Signer won't send it again: in production, keep it in
      // your storage and retry it from there.
      console.error(`Could not process ${key}: ${err.message}`)
    );
  });
});

server.listen(port, "localhost", () =>
  console.log(`Listening on http://localhost:${port}${webhookPath}. Press Ctrl+C to stop.`)
);

function isAuthorized(authorization: string | undefined): boolean {
  // timingSafeEqual needs inputs of the same length, so compare SHA-256 hashes. A constant-time
  // comparison keeps the response time from revealing how much of the token matched.
  const hash = (value: string) => crypto.createHash("sha256").update(value).digest();
  return crypto.timingSafeEqual(hash(authorization ?? ""), hash(expectedAuthorization));
}

function parseWebhook(body: string): WebhooksWebhookModel | undefined {
  try {
    const value = JSON.parse(body);
    return typeof value === "object" && value !== null ? value : undefined;
  } catch {
    return undefined;
  }
}

// The key of an event: its type, the document id and, for an event about one participant's
// action, the flow action id (a document gets one DocumentSigned per signer).
function getEventKey(webhook: WebhooksWebhookModel): string {
  const data = webhook.data ?? {};
  // DocumentsCreated and DocumentDeleted carry a list of documents instead of one id.
  const documentId: string =
    data.id ?? (data.documents ?? []).map((document: { id: string }) => document.id).join(",");
  const action = data.signature ?? data.approval ?? data.refusal;
  return [webhook.type, documentId, action?.flowActionId].filter((part) => part).join(":");
}

async function processEvent(webhook: WebhooksWebhookModel) {
  if (webhook.type === WebhookTypes.DocumentConcluded) {
    // 4. Events can arrive out of order, and the payload is a notification, not the source of
    //    truth: read the document and act only if it is really concluded.
    const document = (await documentsApi.apiDocumentsIdGet(webhook.data.id)).data;
    if (document.status !== DocumentStatus.Concluded) {
      console.log(`Document ${document.id} is ${document.status}, not Concluded: nothing to do yet`);
      return;
    }
  }

  // The switch on the event type from checkDocumentStatusScenario.ts.
  handleWebhook(webhook);
}
