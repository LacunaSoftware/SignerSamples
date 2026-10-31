import { getBase64, UploadApi } from "signer-node-client";
import * as path from "path";
import { callSignerApi, config } from "./scenario";

/**
 * This scenario sets, for one document, two settings that otherwise come from the organization:
 *
 * - signerPositioningEnabled: whether the signer may place their own signature on the page.
 * - automaticSignaturePositioning: what Signer does with a signature that nobody placed: Footnote
 *   puts it in the page footer, SignatureSheet on a signature sheet at the end of the PDF, and
 *   Disabled leaves it off. Disabled can't be combined with signerPositioningEnabled = false.
 *
 * It sets them when it creates the document, then changes them with PUT /signer-positioning, which
 * works until a participant signs or approves. A null value takes the organization's current
 * setting, and the document keeps it even if the organization changes later.
 *
 * signer-node-client 1.1.0 has neither setting, so the scenario calls the REST API directly
 * (callSignerApi, in scenario.ts).
 */
const uploadApi = new UploadApi(config);

async function printSettings(documentId: string) {
  const document = await callSignerApi("GET", `/api/documents/${documentId}`);
  console.log(`Document ${documentId}: signerPositioningEnabled = ${document.signerPositioningEnabled}, automaticSignaturePositioning = ${document.automaticSignaturePositioning}`);
}

async function run() {
  // 1. Upload the file with the client library, as in the other scenarios.
  const filePath = path.join(__dirname, "..", "samples", "sample.pdf");
  const upload = await uploadApi.apiUploadsBytesPost({ bytes: getBase64(filePath) });

  // 2. Create the document: the signer places their signature, and a signature nobody placed goes
  //    on a signature sheet.
  const results = await callSignerApi("POST", "/api/documents", {
    files: [
      { id: upload.data.id, name: "sample.pdf", contentType: "application/pdf", displayName: "Signer Positioning Sample" },
    ],
    flowActions: [
      {
        type: "Signer",
        user: { name: "Jack Bauer", email: "jack.bauer@mailinator.com", identifier: "75502846369" },
      },
    ],
    signerPositioningEnabled: true,
    automaticSignaturePositioning: "SignatureSheet",
  });
  const documentId = results[0].documentId;
  await printSettings(documentId);

  // 3. Before the first signature, change them: the signer can't move the signature any more, and
  //    an unplaced one goes in a footnote.
  await callSignerApi("PUT", `/api/documents/${documentId}/signer-positioning`, {
    signerPositioningEnabled: false,
    automaticSignaturePositioning: "Footnote",
  });
  await printSettings(documentId);
}

run().catch((error) => console.error(error));
