package com.lacunasoftware.signer.sample.scenarios;

import java.io.IOException;
import java.util.UUID;

import com.google.gson.JsonObject;
import com.lacunasoftware.signer.javaclient.exceptions.RestException;

/**
 * This scenario pauses a pending document and then resumes it. While a document is paused, nobody can
 * sign, approve or refuse it, Signer sends no reminders and the document doesn't expire; you can still
 * cancel it. Resuming lets the flow continue where it stopped. Each step e-mails the participants whose
 * turn has come and sends a webhook event, DocumentPaused or DocumentResumed.
 *
 * signer-client 2.13.0 has no pause or resume methods, so the scenario calls the REST API directly
 * (callSignerApi, in Scenario.java). It reads the document the same way: the client's DocumentStatus
 * has no Paused value.
 */
public class PauseAndResumeDocumentScenario extends Scenario {
    @Override
    public void Run() throws IOException, RestException {
        // 1. You need the id of a pending document.
        UUID documentId = createDocument().getDocumentId();

        // 2. Pause it. The reason is optional; Signer shows it on the document as statusUpdateReason.
        JsonObject pauseRequest = new JsonObject();
        pauseRequest.addProperty("reason", "Waiting for the contract review");
        callSignerApi("POST", "/api/documents/" + documentId + "/pause", pauseRequest);

        JsonObject document = callSignerApi("GET", "/api/documents/" + documentId, null).getAsJsonObject();
        System.out.println(String.format("Document %s is %s: %s", documentId, document.get("status").getAsString(), document.get("statusUpdateReason").getAsString()));

        // 3. Resume it. The request has no body.
        callSignerApi("POST", "/api/documents/" + documentId + "/resume", null);

        document = callSignerApi("GET", "/api/documents/" + documentId, null).getAsJsonObject();
        System.out.println(String.format("Document %s is %s", documentId, document.get("status").getAsString()));
    }
}
