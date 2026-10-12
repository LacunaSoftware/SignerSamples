package com.lacunasoftware.signer.sample.scenarios;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.lacunasoftware.signer.DocumentStatus;
import com.lacunasoftware.signer.WebhookTypes;
import com.lacunasoftware.signer.documents.DocumentModel;
import com.lacunasoftware.signer.webhooks.WebhookModel;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

/**
 * A minimal webhook endpoint, built on the JDK's com.sun.net.httpserver (no web framework). It
 * listens on http://localhost:8080/webhook and follows the rules of
 * https://docs.lacunasoftware.com/en-us/articles/signer/webhooks#best-practices:
 *
 * 1. Check the auth header registered with the webhook: Signer does not sign the body.
 * 2. Answer 200 at once and process the event afterwards. Signer retries a non-2xx answer or a
 *    connection error (up to 10 times by default), so slow work before answering causes retries.
 * 3. Be idempotent: the same event can arrive more than once.
 * 4. Don't trust the order of events: read the document from Signer before acting on it.
 *
 * Select it in App.java and run ./gradlew run. The README shows how to test it with curl, and how
 * to receive real events through an HTTPS tunnel.
 */
public class WebhookEndpointScenario extends Scenario {
    private static final int PORT = 8080;
    private static final String WEBHOOK_PATH = "/webhook";

    // The token registered with the webhook under Organization > Integration, with the auth type
    // "Bearer": Signer then sends "Authorization: Bearer <token>". Use a long random value of your own.
    private static final String EXPECTED_AUTHORIZATION = "Bearer my-webhook-token";

    // Keys of the events already received. A sample can keep them in memory; production needs durable
    // storage, for example a table with a unique index on the key, shared by every instance of the app.
    private final Set<String> receivedEvents = ConcurrentHashMap.newKeySet();

    // Processes the events after they are answered, off the server's thread.
    private final ExecutorService processor = Executors.newSingleThreadExecutor();

    // Its HandleWebhook method has the switch on the event type.
    private final CheckDocumentStatusScenario documentEventsHandler = new CheckDocumentStatusScenario();

    // signerClient.getGson() reads the API's enums and dates.
    private Gson gson;

    @Override
    public void Run() throws Exception {
        gson = signerClient.getGson();
        documentEventsHandler.Init();

        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext(WEBHOOK_PATH, this::handleRequest);
        server.start();
        System.out.println(String.format("Listening on http://localhost:%d%s. Press Ctrl+C to stop.", PORT, WEBHOOK_PATH));
    }

    private void handleRequest(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod()) || !WEBHOOK_PATH.equals(exchange.getRequestURI().getPath())) {
            respond(exchange, 404);
            return;
        }

        // 1. Check the auth header before reading the body.
        if (!isAuthorized(exchange.getRequestHeaders().getFirst("Authorization"))) {
            respond(exchange, 401);
            return;
        }

        // The body is { "type": "...", "data": { ... } }, with camelCase fields.
        JsonElement body = readJson(exchange);
        if (body == null || !body.isJsonObject()) {
            respond(exchange, 400);
            return;
        }

        // 3. Answer 200 to an event already received, so that Signer stops sending it, but don't
        //    process it again.
        String key = getEventKey(gson.fromJson(body, Event.class));
        if (!receivedEvents.add(key)) {
            System.out.println("Ignored a repeated event: " + key);
            respond(exchange, 200);
            return;
        }

        // 2. Answer now, then process the event.
        respond(exchange, 200);
        System.out.println("Accepted: " + key);
        processor.execute(() -> processEvent(body, key));
    }

    private void processEvent(JsonElement body, String key) {
        try {
            WebhookModel webhook = gson.fromJson(body, WebhookModel.class);
            if (webhook.getType() == null) {
                System.out.println("This sample does not handle the event " + key);
                return;
            }

            if (webhook.getType() == WebhookTypes.DOCUMENTCONCLUDED) {
                // 4. Events can arrive out of order, and the payload is a notification, not the source
                //    of truth: read the document and act only if it is really concluded.
                String documentId = body.getAsJsonObject().getAsJsonObject("data").get("id").getAsString();
                DocumentModel document = signerClient.getDocumentDetails(UUID.fromString(documentId));
                if (document.getStatus() != DocumentStatus.CONCLUDED) {
                    System.out.println(String.format("Document %s is %s, not Concluded: nothing to do yet", document.getId(), document.getStatus()));
                    return;
                }
            }

            documentEventsHandler.HandleWebhook(webhook);
        } catch (Exception e) {
            // The event was already answered, so Signer won't send it again: in production, keep it in
            // your storage and retry it from there.
            System.out.println(String.format("Could not process %s: %s", key, e));
        }
    }

    private static boolean isAuthorized(String authorization) {
        // MessageDigest.isEqual compares in constant time, so the response time doesn't reveal how much
        // of the token matched.
        return authorization != null && MessageDigest.isEqual(
                authorization.getBytes(StandardCharsets.UTF_8),
                EXPECTED_AUTHORIZATION.getBytes(StandardCharsets.UTF_8));
    }

    private JsonElement readJson(HttpExchange exchange) {
        try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, JsonElement.class);
        } catch (JsonParseException | IOException e) {
            return null;
        }
    }

    // The key of an event: its type, the document id and, for an event about one participant's
    // action, the flow action id (a document gets one DocumentSigned per signer).
    private static String getEventKey(Event event) {
        EventData data = event.data != null ? event.data : new EventData();
        String documentId = data.id;
        if (documentId == null && data.documents != null) {
            documentId = data.documents.stream().map(document -> document.id).collect(Collectors.joining(","));
        }
        FlowAction action = data.signature != null ? data.signature : data.approval != null ? data.approval : data.refusal;
        String flowActionId = action != null ? action.flowActionId : null;
        return Stream.of(event.type, documentId, flowActionId)
                .filter(part -> part != null && !part.isEmpty())
                .collect(Collectors.joining(":"));
    }

    private static void respond(HttpExchange exchange, int statusCode) throws IOException {
        exchange.sendResponseHeaders(statusCode, -1); // -1: no body
        exchange.close();
    }

    // The fields of the body that make up the event key; Gson ignores the others.
    private static class Event {
        String type;
        EventData data;
    }

    private static class EventData {
        String id;
        List<EventData> documents; // DocumentsCreated and DocumentDeleted carry a list of documents instead of one id.
        FlowAction signature;
        FlowAction approval;
        FlowAction refusal;
    }

    private static class FlowAction {
        String flowActionId;
    }
}
