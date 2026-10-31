package com.lacunasoftware.signer.sample.scenarios;

import java.io.IOException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lacunasoftware.signer.javaclient.exceptions.RestException;
import com.lacunasoftware.signer.javaclient.models.UploadModel;
import com.lacunasoftware.signer.sample.Util;

/**
 * This scenario chooses the language of each e-mail that Signer sends about a document:
 *
 * - notificationLanguage on a flow action: the e-mails to that participant.
 * - notifiedEmailsData on the document: the addresses told when the document is concluded, each with
 *   its own language. It replaces the older notifiedEmails list of addresses.
 *
 * The languages are Pt, Es and En; without one, Signer uses the instance's default language.
 *
 * signer-client has neither field, so the scenario creates the document with a direct call to
 * the REST API (callSignerApi, in Scenario.java).
 */
public class CreateDocumentWithNotificationLanguageScenario extends Scenario {
    @Override
    public void Run() throws IOException, RestException {
        // 1. Upload the file with the client library, as in the other scenarios.
        byte[] content = Util.getInstance().getResourceFile("sample.pdf");
        UploadModel uploadModel = signerClient.uploadFile("sample.pdf", content, "application/pdf");

        // 2. Create the document: one signer is notified in English, the other in Spanish, and a third
        //    address learns in Portuguese that the document is concluded.
        JsonObject flowActionOne = signerJson("Jack Bauer", "jack.bauer@mailinator.com", "75502846369");
        flowActionOne.addProperty("notificationLanguage", "En");
        JsonObject flowActionTwo = signerJson("James Bond", "james.bond@mailinator.com", "95588148061");
        flowActionTwo.addProperty("notificationLanguage", "Es");

        JsonObject notifiedEmail = new JsonObject();
        notifiedEmail.addProperty("email", "jill.palmer@mailinator.com");
        notifiedEmail.addProperty("notificationLanguage", "Pt");

        JsonArray files = new JsonArray();
        files.add(fileJson(uploadModel, "Notification Language Sample"));
        JsonArray flowActions = new JsonArray();
        flowActions.add(flowActionOne);
        flowActions.add(flowActionTwo);
        JsonArray notifiedEmailsData = new JsonArray();
        notifiedEmailsData.add(notifiedEmail);
        JsonObject request = new JsonObject();
        request.add("files", files);
        request.add("flowActions", flowActions);
        request.add("notifiedEmailsData", notifiedEmailsData);

        String documentId = callSignerApi("POST", "/api/documents", request)
            .getAsJsonArray().get(0).getAsJsonObject().get("documentId").getAsString();

        // 3. Read the document back.
        JsonObject document = callSignerApi("GET", "/api/documents/" + documentId, null).getAsJsonObject();
        for (JsonElement flowAction : document.getAsJsonArray("flowActions")) {
            JsonObject action = flowAction.getAsJsonObject();
            System.out.println(String.format("%s is notified in %s",
                action.getAsJsonObject("user").get("email").getAsString(), requiredField(action, "notificationLanguage").getAsString()));
        }
        for (JsonElement element : requiredField(document, "notifiedEmailsData").getAsJsonArray()) {
            JsonObject email = element.getAsJsonObject();
            System.out.println(String.format("%s learns of the conclusion in %s",
                email.get("email").getAsString(), requiredField(email, "notificationLanguage").getAsString()));
        }
    }
}
