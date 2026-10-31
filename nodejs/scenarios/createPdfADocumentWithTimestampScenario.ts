import { getBase64, UploadApi } from "signer-node-client";
import * as path from "path";
import { callSignerApi, config } from "./scenario";

/**
 * This scenario creates a document that Signer converts to PDF/A, for long-term archiving, and
 * whose signatures get a timestamp:
 *
 * - convertToPdfA and pdfAProfile: Signer converts the uploaded PDF before the flow starts. The
 *   result then has a new uploadId, not the one you sent: use documentId.
 * - timestampEnabled: overrides the organization's timestamp setting for this document. true
 *   timestamps every signature and false none; leave it out to use the organization's setting. On
 *   an instance without a timestamper, Signer rejects a document that sets it.
 *
 * signer-node-client 1.1.0 has none of these fields, so the scenario creates the document with a
 * direct call to the REST API (callSignerApi, in scenario.ts).
 */
const uploadApi = new UploadApi(config);

async function run() {
  // 1. Upload the file with the client library, as in the other scenarios.
  const filePath = path.join(__dirname, "..", "samples", "sample.pdf");
  const upload = await uploadApi.apiUploadsBytesPost({ bytes: getBase64(filePath) });

  // 2. Create the document. The profiles go from PdfA1a to PdfA4f; PdfA2b is a common choice for
  //    archiving.
  const results = await callSignerApi("POST", "/api/documents", {
    files: [
      { id: upload.data.id, name: "sample.pdf", contentType: "application/pdf", displayName: "PDF/A With Timestamp Sample" },
    ],
    flowActions: [
      {
        type: "Signer",
        user: { name: "Jack Bauer", email: "jack.bauer@mailinator.com", identifier: "75502846369" },
      },
    ],
    convertToPdfA: true,
    pdfAProfile: "PdfA2b",
    timestampEnabled: true,
  });
  const documentId = results[0].documentId;

  // 3. Read the document back.
  const document = await callSignerApi("GET", `/api/documents/${documentId}`);
  console.log(`Document ${documentId}: timestampEnabled = ${document.timestampEnabled}`);
}

run().catch((error) => console.error(error));
