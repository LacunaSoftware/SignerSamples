import { getBase64, UploadApi } from "signer-node-client";
import * as path from "path";
import { callSignerApi, config } from "./scenario";

/**
 * This scenario chooses the language of each e-mail that Signer sends about a document:
 *
 * - notificationLanguage on a flow action: the e-mails to that participant.
 * - notifiedEmailsData on the document: the addresses told when the document is concluded, each
 *   with its own language. It replaces the older notifiedEmails list of addresses.
 *
 * The languages are Pt, Es and En; without one, Signer uses the instance's default language.
 *
 * signer-node-client 1.1.0 has neither field, so the scenario creates the document with a direct
 * call to the REST API (callSignerApi, in scenario.ts).
 */
const uploadApi = new UploadApi(config);

async function run() {
  // 1. Upload the file with the client library, as in the other scenarios.
  const filePath = path.join(__dirname, "..", "samples", "sample.pdf");
  const upload = await uploadApi.apiUploadsBytesPost({ bytes: getBase64(filePath) });

  // 2. Create the document: one signer is notified in English, the other in Spanish, and a third
  //    address learns in Portuguese that the document is concluded.
  const results = await callSignerApi("POST", "/api/documents", {
    files: [
      { id: upload.data.id, name: "sample.pdf", contentType: "application/pdf", displayName: "Notification Language Sample" },
    ],
    flowActions: [
      {
        type: "Signer",
        user: { name: "Jack Bauer", email: "jack.bauer@mailinator.com", identifier: "75502846369" },
        notificationLanguage: "En",
      },
      {
        type: "Signer",
        user: { name: "James Bond", email: "james.bond@mailinator.com", identifier: "95588148061" },
        notificationLanguage: "Es",
      },
    ],
    notifiedEmailsData: [
      { email: "jill.palmer@mailinator.com", notificationLanguage: "Pt" },
    ],
  });
  const documentId = results[0].documentId;

  // 3. Read the document back.
  const document = await callSignerApi("GET", `/api/documents/${documentId}`);
  for (const flowAction of document.flowActions) {
    console.log(`${flowAction.user.email} is notified in ${flowAction.notificationLanguage}`);
  }
  for (const notifiedEmail of document.notifiedEmailsData) {
    console.log(`${notifiedEmail.email} learns of the conclusion in ${notifiedEmail.notificationLanguage}`);
  }
}

run().catch((error) => console.error(error));
