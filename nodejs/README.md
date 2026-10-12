# Samples for the Nodejs signer client library
These samples were tested with Node v.19.0.0

## Building
First, run `npm install` on this folder to install the client library, TypeScript and ts-node.

## How to test a sample
Each scenario is a TypeScript script that runs with ts-node; nothing is compiled ahead of time.
From this folder, run
```
npx ts-node scenarios/[sample-name].ts
```
The scenarios open their input files in [`samples`](samples) relative to their own folder
(`__dirname`), so they find them whatever the working directory is.
To type-check every scenario without running any, run `npx tsc --noEmit -p scenarios`.

## Samples in this project

Each `.ts` file in [`scenarios`](scenarios) is one Lacuna Signer integration task, calling the
Signer API through the `signer-node-client` npm package (`^1.1.0` in [`package.json`](package.json)).
The table is generated from the repository's catalog ([samples.json](../samples.json), also shown
as [SAMPLES.md](../SAMPLES.md)), where each slug has its docs link and the same task in other
languages. Before changing anything, read [AGENTS.md](../AGENTS.md).

<!-- BEGIN GENERATED SAMPLES LIST (project: nodejs) -->
| Sample | Area | Entry file | Status | Command |
|---|---|---|---|---|
| Sign a document: create it with one signer<br>`sign-a-document` | Create documents | [createDocumentWithOneSignerScenario.ts](scenarios/createDocumentWithOneSignerScenario.ts) | `current` | `npm install && npx ts-node scenarios/createDocumentWithOneSignerScenario.ts` |
| Create a document with an attachment<br>`create-document-with-attachment` | Create documents | [createDocumentWithAttachmentScenario.ts](scenarios/createDocumentWithAttachmentScenario.ts) | `current` | `npm install && npx ts-node scenarios/createDocumentWithAttachmentScenario.ts` |
| Create a document with a description<br>`create-document-with-description` | Create documents | [createDocumentWithDescriptionScenario.ts](scenarios/createDocumentWithDescriptionScenario.ts) | `current` | `npm install && npx ts-node scenarios/createDocumentWithDescriptionScenario.ts` |
| Merge several PDFs into one document (envelope)<br>`create-envelope` | Create documents | [createDocumentWithEnvelopeScenario.ts](scenarios/createDocumentWithEnvelopeScenario.ts) | `current` | `npm install && npx ts-node scenarios/createDocumentWithEnvelopeScenario.ts` |
| Upload a file as multipart/form-data<br>`upload-file-multipart` | Create documents | [uploadUsingMultipartFormDataScenario.ts](scenarios/uploadUsingMultipartFormDataScenario.ts) | `current` | `npm install && npx ts-node scenarios/uploadUsingMultipartFormDataScenario.ts` |
| Create a document with two or more signers, in any order<br>`create-document-multiple-signers` | Flows: participants, order and rules | [createDocumentWithTwoOrMoreSignersWithoutOrderScenario.ts](scenarios/createDocumentWithTwoOrMoreSignersWithoutOrderScenario.ts) | `current` | `npm install && npx ts-node scenarios/createDocumentWithTwoOrMoreSignersWithoutOrderScenario.ts` |
| Create a document with signers in a fixed order<br>`create-document-ordered-flow` | Flows: participants, order and rules | [createDocumentWithTwoOrMoreSignersWithOrderScenario.ts](scenarios/createDocumentWithTwoOrMoreSignersWithOrderScenario.ts) | `current` | `npm install && npx ts-node scenarios/createDocumentWithTwoOrMoreSignersWithOrderScenario.ts` |
| Create a document with an approver<br>`create-document-with-approver` | Flows: participants, order and rules | [createDocumentWithApproversScenario.ts](scenarios/createDocumentWithApproversScenario.ts) | `current` | `npm install && npx ts-node scenarios/createDocumentWithApproversScenario.ts` |
| Create a document with a signing rule (any N of a group)<br>`create-document-with-signing-rule` | Flows: participants, order and rules | [createDocumentWithSigningRuleScenario.ts](scenarios/createDocumentWithSigningRuleScenario.ts) | `current` | `npm install && npx ts-node scenarios/createDocumentWithSigningRuleScenario.ts` |
| Pre-position a signature on the document<br>`create-document-with-positioned-signatures` | Flows: participants, order and rules | [createDocumentWithPositionedSignaturesScenario.ts](scenarios/createDocumentWithPositionedSignaturesScenario.ts) | `current` | `npm install && npx ts-node scenarios/createDocumentWithPositionedSignaturesScenario.ts` |
| Require a CAdES signature on a PDF<br>`create-pdf-with-cades-signature` | Signature formats (CAdES, XAdES) | [createPDFDocumentWithCadesSignatureScenario.ts](scenarios/createPDFDocumentWithCadesSignatureScenario.ts) | `current` | `npm install && npx ts-node scenarios/createPDFDocumentWithCadesSignatureScenario.ts` |
| Sign a whole XML file (XAdES)<br>`create-xml-full-signature` | Signature formats (CAdES, XAdES) | [createXmlWithFullDocumentSignatureScenario.ts](scenarios/createXmlWithFullDocumentSignatureScenario.ts) | `current` | `npm install && npx ts-node scenarios/createXmlWithFullDocumentSignatureScenario.ts` |
| Sign one element of an XML file (XAdES)<br>`create-xml-element-signature` | Signature formats (CAdES, XAdES) | [createXMLWithElementSignatureScenario.ts](scenarios/createXMLWithElementSignatureScenario.ts) | `current` | `npm install && npx ts-node scenarios/createXMLWithElementSignatureScenario.ts` |
| Sign a Brazilian digital diploma (XAdES, Portaria MEC 554/2019)<br>`create-digital-degree` | Signature formats (CAdES, XAdES) | [createDigitalDegreeScenario.ts](scenarios/createDigitalDegreeScenario.ts) | `current` | `npm install && npx ts-node scenarios/createDigitalDegreeScenario.ts` |
| Sign with a digital certificate through the API (public signature)<br>`public-signature-with-certificate` | Sign through the API | [publicSignatureScenario.ts](scenarios/publicSignatureScenario.ts) | `current` | `npm install && npx ts-node scenarios/publicSignatureScenario.ts` |
| Sign without leaving your page (embedded signature)<br>`embedded-signature` | Embedded signing | [embeddedSignatureScenario.ts](scenarios/embeddedSignatureScenario.ts) | `current` | `npm install && npx ts-node scenarios/embeddedSignatureScenario.ts` |
| Send reminders to participants with pending actions<br>`send-signing-reminders` | Track and list documents | [notifyFlowParticipantsScenario.ts](scenarios/notifyFlowParticipantsScenario.ts) | `current` | `npm install && npx ts-node scenarios/notifyFlowParticipantsScenario.ts` |
| List saved document flows<br>`list-document-flows` | Track and list documents | [listDocumentScenario.ts](scenarios/listDocumentScenario.ts) | `current` | `npm install && npx ts-node scenarios/listDocumentScenario.ts` |
| Receive webhooks with a minimal HTTP endpoint<br>`webhook-endpoint` | Webhooks | [webhookEndpointScenario.ts](scenarios/webhookEndpointScenario.ts) | `current` | `npm install && npx ts-node scenarios/webhookEndpointScenario.ts` |
| Handle document webhooks (signed, approved, refused, concluded)<br>`webhook-document-events` | Webhooks | [checkDocumentStatusScenario.ts](scenarios/checkDocumentStatusScenario.ts) | `current` | `npm install && npx ts-node scenarios/checkDocumentStatusScenario.ts` |
| Download a document's versions<br>`download-document-versions` | Download documents | [downloadDocumentVersionScenario.ts](scenarios/downloadDocumentVersionScenario.ts) | `current` | `npm install && npx ts-node scenarios/downloadDocumentVersionScenario.ts` |
| Cancel a document<br>`cancel-document` | Document lifecycle | [cancelDocumentScenario.ts](scenarios/cancelDocumentScenario.ts) | `current` | `npm install && npx ts-node scenarios/cancelDocumentScenario.ts` |
| Refuse a document as the organization<br>`refuse-document` | Document lifecycle | [refuseDocumentScenario.ts](scenarios/refuseDocumentScenario.ts) | `current` | `npm install && npx ts-node scenarios/refuseDocumentScenario.ts` |
| Delete a document<br>`delete-document` | Document lifecycle | [deleteDocumentScenario.ts](scenarios/deleteDocumentScenario.ts) | `current` | `npm install && npx ts-node scenarios/deleteDocumentScenario.ts` |
| Upload a new version of a document<br>`add-document-version` | Document lifecycle | [addNewDocumentVersionScenario.ts](scenarios/addNewDocumentVersionScenario.ts) | `current` | `npm install && npx ts-node scenarios/addNewDocumentVersionScenario.ts` |
| Create a document in an existing folder<br>`create-document-in-existing-folder` | Folders | [createDocumentInExistingFolderScenario.ts](scenarios/createDocumentInExistingFolderScenario.ts) | `current` | `npm install && npx ts-node scenarios/createDocumentInExistingFolderScenario.ts` |
<!-- END GENERATED SAMPLES LIST -->

## Running the samples

The table's Command column has the exact command for each scenario; run it from this folder.
Compiled JavaScript is not committed, and `scenarios/dist` is git-ignored: compiled output there
would look for `samples` in the wrong folder, so run the `.ts` files with ts-node. Every run
creates real documents on the configured Signer instance, and Signer e-mails the sample
participants (made-up `@mailinator.com` addresses). `downloadDocumentVersionScenario` saves the
downloaded files as `samples/downloaded-*.pdf`, which are also git-ignored.

## Configuration

[`scenarios/scenario.ts`](scenarios/scenario.ts) builds the exported `config` from two
environment variables: `SIGNER_ENDPOINT` (`basePath`, the Signer instance URL) and
`SIGNER_API_KEY` (`apiKey`). When a variable is unset or empty, it falls back to the Signer
sandbox, `https://signer-lac.azurewebsites.net`, and a shared sample key, so the samples run as
they are. The sandbox is where you should develop and test: to use your own organization there,
set `SIGNER_API_KEY` to one of your keys. For production, set both variables together.
[`.env.example`](.env.example) lists every value the project uses, including the webhook
endpoint's; nothing loads that file automatically.

Set the variables in the shell that runs ts-node. In bash, quoting the key because of its `|`:

```bash
export SIGNER_ENDPOINT=https://signer-lac.azurewebsites.net
export SIGNER_API_KEY='your-application|your-key'
npx ts-node scenarios/createDocumentWithOneSignerScenario.ts
```

In PowerShell:

```powershell
$env:SIGNER_ENDPOINT = "https://signer-lac.azurewebsites.net"
$env:SIGNER_API_KEY = "your-application|your-key"
npx ts-node scenarios/createDocumentWithOneSignerScenario.ts
```

Prerequisites of some scenarios:

- `createDocumentInExistingFolderScenario`: the organization needs a folder that matches
  "Sample folder".
- `publicSignatureScenario`: uses the Lacuna test certificate
  [`samples/56072386105.pfx`](samples/56072386105.pfx) (password `1234`).

## Receive webhooks locally

[`webhookEndpointScenario.ts`](scenarios/webhookEndpointScenario.ts) is a minimal webhook
endpoint built on Node's `http` module. It listens on `http://localhost:8080/webhook` (set
`SIGNER_WEBHOOK_PORT` for another port) and follows the
[webhooks docs](https://docs.lacunasoftware.com/en-us/articles/signer/webhooks#best-practices):

1. It answers 401 unless the `Authorization` header is `Bearer <token>` with the token you
   registered, set in `SIGNER_WEBHOOK_TOKEN` (default `my-webhook-token`), because Signer does not
   sign the body.
2. It answers 200 at once and processes the event afterwards. Signer retries a non-2xx answer or a
   connection error, up to 10 times by default.
3. It ignores an event it has already received, keyed on `type`, the document `id` and, when there
   is one, `flowActionId`. It keeps the keys in memory; production needs durable storage.
4. Events can arrive out of order, so before acting on `DocumentConcluded` it reads the document
   back (`GET /api/documents/{id}`) and checks that its `status` is `Concluded`. This is its only
   call to Signer.

It then passes the body to `handleWebhook`, exported by
[`checkDocumentStatusScenario.ts`](scenarios/checkDocumentStatusScenario.ts), which switches on
`type` and reads `data` as the matching model.

To test it without Signer, run `npx ts-node scenarios/webhookEndpointScenario.ts`, and post a
`DocumentSigned` event with the default token from bash (Git Bash on Windows):

```bash
curl -i http://localhost:8080/webhook \
  -H "Authorization: Bearer my-webhook-token" \
  -H "Content-Type: application/json" \
  -d '{"type":"DocumentSigned","data":{"id":"3fa85f64-5717-4562-b3fc-2c963f66afa6","name":"sample.pdf",
       "signature":{"flowActionId":"5c1a2b3c-4d5e-4f60-8a7b-9c0d1e2f3a4b","name":"Jack Bauer",
       "identifier":"75502846369","emailAddress":"jack.bauer@mailinator.com"}}}'
```

The answer is `200`, and the console prints `Accepted: ...` and `Document ... is signed`. Post it
again and the console prints `Ignored a repeated event`; change the token and the answer is `401`.
A `DocumentConcluded` body, `{"type":"DocumentConcluded","data":{"id":"<document id>"}}`, makes
the endpoint read that document from Signer after answering; with a made-up id, that call fails.

To receive real events:

1. Expose port 8080 with an HTTPS tunnel (ngrok, Cloudflare Tunnel, ...), for example
   `ngrok http 8080`, because Signer calls the endpoint from its own servers.
2. In Signer, under **Organization → Integration**, register `https://<tunnel host>/webhook` with
   the auth type `Bearer` and a long random token, or call
   `POST /api/organizations/{orgId}/webhooks` with `url`, `authType` and `token`. Set
   `SIGNER_WEBHOOK_TOKEN` to the same token (see Configuration).
3. Webhooks belong to an organization (personal organizations have none), so register it in your
   own organization and create the documents there, with its API key (see Configuration).
