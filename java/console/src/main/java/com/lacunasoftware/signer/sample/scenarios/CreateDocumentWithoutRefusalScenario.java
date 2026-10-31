package com.lacunasoftware.signer.sample.scenarios;

import java.io.IOException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lacunasoftware.signer.javaclient.exceptions.RestException;
import com.lacunasoftware.signer.javaclient.models.UploadModel;
import com.lacunasoftware.signer.sample.Util;

/**
 * This scenario creates a document that one of its two signers cannot refuse. Signer hides the refuse
 * option from that participant, on the signing and approval pages, and rejects a refusal from them.
 * The setting is per participant: the other signer can still refuse, and your application can still
 * refuse the document on the organization's behalf (RefuseDocumentScenario).
 *
 * FlowActionCreateModel in signer-client 2.13.0 has no disableRefusal, so the scenario creates the
 * document with a direct call to the REST API (callSignerApi, in Scenario.java).
 */
public class CreateDocumentWithoutRefusalScenario extends Scenario {
    @Override
    public void Run() throws IOException, RestException {
        // 1. Upload the file with the client library, as in the other scenarios.
        byte[] content = Util.getInstance().getResourceFile("sample.pdf");
        UploadModel uploadModel = signerClient.uploadFile("sample.pdf", content, "application/pdf");

        // 2. Create the document. Set disableRefusal on each flow action whose participant must not
        //    refuse; it defaults to false.
        JsonObject flowActionOne = signerJson("Jack Bauer", "jack.bauer@mailinator.com", "75502846369");
        flowActionOne.addProperty("disableRefusal", true);
        JsonObject flowActionTwo = signerJson("James Bond", "james.bond@mailinator.com", "95588148061");

        JsonArray files = new JsonArray();
        files.add(fileJson(uploadModel, "Without Refusal Sample"));
        JsonArray flowActions = new JsonArray();
        flowActions.add(flowActionOne);
        flowActions.add(flowActionTwo);
        JsonObject request = new JsonObject();
        request.add("files", files);
        request.add("flowActions", flowActions);

        // One result per file.
        JsonElement results = callSignerApi("POST", "/api/documents", request);
        String documentId = results.getAsJsonArray().get(0).getAsJsonObject().get("documentId").getAsString();

        // 3. Read the document back: each flow action has disableRefusal.
        JsonObject document = callSignerApi("GET", "/api/documents/" + documentId, null).getAsJsonObject();
        for (JsonElement flowAction : document.getAsJsonArray("flowActions")) {
            JsonObject action = flowAction.getAsJsonObject();
            System.out.println(String.format("%s can refuse: %s",
                action.getAsJsonObject("user").get("name").getAsString(),
                !action.get("disableRefusal").getAsBoolean()));
        }
    }
}
