package com.lacunasoftware.signer.sample.scenarios;

import java.io.IOException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lacunasoftware.signer.javaclient.exceptions.RestException;
import com.lacunasoftware.signer.javaclient.models.UploadModel;
import com.lacunasoftware.signer.sample.Util;

/**
 * This scenario creates a document that Signer converts to PDF/A, for long-term archiving, and whose
 * signatures get a timestamp:
 *
 * - convertToPdfA and pdfAProfile: Signer converts the uploaded PDF before the flow starts. The result
 *   then has a new uploadId, not the one you sent: use documentId.
 * - timestampEnabled: overrides the organization's timestamp setting for this document. true
 *   timestamps every signature and false none; leave it out to use the organization's setting. On an
 *   instance without a timestamper, Signer rejects a document that sets it.
 *
 * signer-client has none of these fields, so the scenario creates the document with a direct
 * call to the REST API (callSignerApi, in Scenario.java).
 */
public class CreatePdfADocumentWithTimestampScenario extends Scenario {
    @Override
    public void Run() throws IOException, RestException {
        // 1. Upload the file with the client library, as in the other scenarios.
        byte[] content = Util.getInstance().getResourceFile("sample.pdf");
        UploadModel uploadModel = signerClient.uploadFile("sample.pdf", content, "application/pdf");

        // 2. Create the document. The profiles go from PdfA1a to PdfA4f; PdfA2b is a common choice for
        //    archiving.
        JsonArray files = new JsonArray();
        files.add(fileJson(uploadModel, "PDF/A With Timestamp Sample"));
        JsonArray flowActions = new JsonArray();
        flowActions.add(signerJson("Jack Bauer", "jack.bauer@mailinator.com", "75502846369"));
        JsonObject request = new JsonObject();
        request.add("files", files);
        request.add("flowActions", flowActions);
        request.addProperty("convertToPdfA", true);
        request.addProperty("pdfAProfile", "PdfA2b");
        request.addProperty("timestampEnabled", true);

        String documentId = callSignerApi("POST", "/api/documents", request)
            .getAsJsonArray().get(0).getAsJsonObject().get("documentId").getAsString();

        // 3. Read the document back.
        JsonObject document = callSignerApi("GET", "/api/documents/" + documentId, null).getAsJsonObject();
        System.out.println(String.format("Document %s: timestampEnabled = %s", documentId, requiredField(document, "timestampEnabled").getAsBoolean()));
    }
}
