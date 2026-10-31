package com.lacunasoftware.signer.sample.scenarios;

import java.io.IOException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lacunasoftware.signer.javaclient.exceptions.RestException;
import com.lacunasoftware.signer.javaclient.models.UploadModel;
import com.lacunasoftware.signer.sample.Util;

/**
 * This scenario sets, for one document, two settings that otherwise come from the organization:
 *
 * - signerPositioningEnabled: whether the signer may place their own signature on the page.
 * - automaticSignaturePositioning: what Signer does with a signature that nobody placed: Footnote puts
 *   it in the page footer, SignatureSheet on a signature sheet at the end of the PDF, and Disabled
 *   leaves it off. Disabled can't be combined with signerPositioningEnabled = false.
 *
 * It sets them when it creates the document, then changes them with
 * PUT /api/documents/{id}/signer-positioning, which works until a participant signs or approves. A
 * null value takes the organization's current setting, and the document keeps it even if the
 * organization changes later.
 *
 * signer-client has neither setting, so the scenario calls the REST API directly
 * (callSignerApi, in Scenario.java).
 */
public class CreateDocumentWithSignerPositioningScenario extends Scenario {
    @Override
    public void Run() throws IOException, RestException {
        // 1. Upload the file with the client library, as in the other scenarios.
        byte[] content = Util.getInstance().getResourceFile("sample.pdf");
        UploadModel uploadModel = signerClient.uploadFile("sample.pdf", content, "application/pdf");

        // 2. Create the document: the signer places their signature, and a signature nobody placed goes
        //    on a signature sheet.
        JsonArray files = new JsonArray();
        files.add(fileJson(uploadModel, "Signer Positioning Sample"));
        JsonArray flowActions = new JsonArray();
        flowActions.add(signerJson("Jack Bauer", "jack.bauer@mailinator.com", "75502846369"));
        JsonObject request = new JsonObject();
        request.add("files", files);
        request.add("flowActions", flowActions);
        request.addProperty("signerPositioningEnabled", true);
        request.addProperty("automaticSignaturePositioning", "SignatureSheet");

        String documentId = callSignerApi("POST", "/api/documents", request)
            .getAsJsonArray().get(0).getAsJsonObject().get("documentId").getAsString();
        printSettings(documentId);

        // 3. Before the first signature, change them: the signer can't move the signature any more, and
        //    an unplaced one goes in a footnote.
        JsonObject settings = new JsonObject();
        settings.addProperty("signerPositioningEnabled", false);
        settings.addProperty("automaticSignaturePositioning", "Footnote");
        callSignerApi("PUT", "/api/documents/" + documentId + "/signer-positioning", settings);
        printSettings(documentId);
    }

    private void printSettings(String documentId) throws IOException {
        JsonObject document = callSignerApi("GET", "/api/documents/" + documentId, null).getAsJsonObject();
        System.out.println(String.format("Document %s: signerPositioningEnabled = %s, automaticSignaturePositioning = %s",
            documentId, requiredField(document, "signerPositioningEnabled").getAsBoolean(),
            requiredField(document, "automaticSignaturePositioning").getAsString()));
    }
}
