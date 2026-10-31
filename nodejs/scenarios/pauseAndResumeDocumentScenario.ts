import { callSignerApi, CreateDocument } from "./scenario";

/**
 * This scenario pauses a pending document and then resumes it. While a document is paused, nobody
 * can sign, approve or refuse it, Signer sends no reminders and the document doesn't expire; you
 * can still cancel it. Resuming lets the flow continue where it stopped. Each step e-mails the
 * participants whose turn has come and sends a webhook event, DocumentPaused or DocumentResumed.
 *
 * signer-node-client 1.1.0 has no pause or resume methods, so the scenario calls the REST API
 * directly (callSignerApi, in scenario.ts). Its DocumentStatus has no Paused value either.
 */
async function run() {
  // 1. You need the id of a pending document.
  const result = await CreateDocument();
  const documentId = result.documentId;

  // 2. Pause it. The reason is optional; Signer shows it on the document as statusUpdateReason.
  await callSignerApi("POST", `/api/documents/${documentId}/pause`, {
    reason: "Waiting for the contract review",
  });

  let document = await callSignerApi("GET", `/api/documents/${documentId}`);
  console.log(`Document ${documentId} is ${document.status}: ${document.statusUpdateReason}`);

  // 3. Resume it. The request has no body.
  await callSignerApi("POST", `/api/documents/${documentId}/resume`);

  document = await callSignerApi("GET", `/api/documents/${documentId}`);
  console.log(`Document ${documentId} is ${document.status}`);
}

run().catch((error) => console.error(error));
