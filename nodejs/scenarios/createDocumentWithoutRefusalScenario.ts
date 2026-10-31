import { getBase64, UploadApi } from "signer-node-client";
import * as path from "path";
import { callSignerApi, config } from "./scenario";

/**
 * This scenario creates a document that one of its two signers cannot refuse. Signer hides the
 * refuse option from that participant, on the signing and approval pages, and rejects a refusal
 * from them. The setting is per participant: the other signer can still refuse, and your
 * application can still refuse the document on the organization's behalf (refuseDocumentScenario.ts).
 *
 * signer-node-client 1.1.0 has no disableRefusal, so the scenario creates the document with a
 * direct call to the REST API (callSignerApi, in scenario.ts).
 */
const uploadApi = new UploadApi(config);

async function run() {
  // 1. Upload the file with the client library, as in the other scenarios.
  const filePath = path.join(__dirname, "..", "samples", "sample.pdf");
  const upload = await uploadApi.apiUploadsBytesPost({ bytes: getBase64(filePath) });

  // 2. Create the document. Set disableRefusal on each flow action whose participant must not
  //    refuse; it defaults to false.
  const results = await callSignerApi("POST", "/api/documents", {
    files: [
      { id: upload.data.id, name: "sample.pdf", contentType: "application/pdf", displayName: "Without Refusal Sample" },
    ],
    flowActions: [
      {
        type: "Signer",
        user: { name: "Jack Bauer", email: "jack.bauer@mailinator.com", identifier: "75502846369" },
        disableRefusal: true,
      },
      {
        type: "Signer",
        user: { name: "James Bond", email: "james.bond@mailinator.com", identifier: "95588148061" },
      },
    ],
  });
  // One result per file.
  const documentId = results[0].documentId;

  // 3. Read the document back: each flow action has disableRefusal.
  const document = await callSignerApi("GET", `/api/documents/${documentId}`);
  for (const flowAction of document.flowActions) {
    console.log(`${flowAction.user.name} can refuse: ${!flowAction.disableRefusal}`);
  }
}

run().catch((error) => console.error(error));
