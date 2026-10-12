# Samples for the Nodejs signer client library
These samples were tested with Node v.19.0.0

## Building
First, run `npm install` to install the client library and other dependencies on this folder

## How to test a sample
First, select a sample, in order to run it, navigate to the `scenarios\dist` folder and execute
```
node [sample-name].js
```
Additionally, if you want to compile all samples to JavaScript before running, navigate to `scenarios` and run
```
tsc
```
After running this command, all js files will be recreated in the `dist` folder.

## Samples in this project

Each `.ts` file in [`scenarios`](scenarios) is one Lacuna Signer integration task, calling the
Signer API through the `signer-node-client` npm package (`^1.1.0` in [`package.json`](package.json)).
The table is generated from the repository's catalog ([samples.json](../samples.json), also shown
as [SAMPLES.md](../SAMPLES.md)), where each slug has its docs link and the same task in other
languages. Before changing anything, read [AGENTS.md](../AGENTS.md).

<!-- BEGIN GENERATED SAMPLES LIST (project: nodejs) -->
| Sample | Area | Entry file | Status | Command |
|---|---|---|---|---|
| Sign a document: create it with one signer<br>`sign-a-document` | Create documents | [createDocumentWithOneSignerScenario.ts](scenarios/createDocumentWithOneSignerScenario.ts) | `current` | `npm install && cd scenarios/dist && node createDocumentWithOneSignerScenario.js` |
| Create a document with an attachment<br>`create-document-with-attachment` | Create documents | [createDocumentWithAttachmentScenario.ts](scenarios/createDocumentWithAttachmentScenario.ts) | `current` | `npm install && cd scenarios/dist && node createDocumentWithAttachmentScenario.js` |
| Create a document with a description<br>`create-document-with-description` | Create documents | [createDocumentWithDescriptionScenario.ts](scenarios/createDocumentWithDescriptionScenario.ts) | `current` | `npm install && cd scenarios/dist && node createDocumentWithDescriptionScenario.js` |
| Merge several PDFs into one document (envelope)<br>`create-envelope` | Create documents | [createDocumentWithEnvelopeScenario.ts](scenarios/createDocumentWithEnvelopeScenario.ts) | `current` | `npm install && cd scenarios/dist && node createDocumentWithEnvelopeScenario.js` |
| Upload a file as multipart/form-data<br>`upload-file-multipart` | Create documents | [uploadUsingMultipartFormDataScenario.ts](scenarios/uploadUsingMultipartFormDataScenario.ts) | `current` | `npm install && cd scenarios/dist && node uploadUsingMultipartFormDataScenario.js` |
| Create a document with two or more signers, in any order<br>`create-document-multiple-signers` | Flows: participants, order and rules | [createDocumentWithTwoOrMoreSignersWithoutOrderScenario.ts](scenarios/createDocumentWithTwoOrMoreSignersWithoutOrderScenario.ts) | `current` | `npm install && cd scenarios/dist && node createDocumentWithTwoOrMoreSignersWithoutOrderScenario.js` |
| Create a document with signers in a fixed order<br>`create-document-ordered-flow` | Flows: participants, order and rules | [createDocumentWithTwoOrMoreSignersWithOrderScenario.ts](scenarios/createDocumentWithTwoOrMoreSignersWithOrderScenario.ts) | `current` | `npm install && cd scenarios/dist && node createDocumentWithTwoOrMoreSignersWithOrderScenario.js` |
| Create a document with an approver<br>`create-document-with-approver` | Flows: participants, order and rules | [createDocumentWithApproversScenario.ts](scenarios/createDocumentWithApproversScenario.ts) | `current` | `npm install && cd scenarios/dist && node createDocumentWithApproversScenario.js` |
| Create a document with a signing rule (any N of a group)<br>`create-document-with-signing-rule` | Flows: participants, order and rules | [createDocumentWithSigningRuleScenario.ts](scenarios/createDocumentWithSigningRuleScenario.ts) | `current` | `npm install && cd scenarios/dist && node createDocumentWithSigningRuleScenario.js` |
| Pre-position a signature on the document<br>`create-document-with-positioned-signatures` | Flows: participants, order and rules | [createDocumentWithPositionedSignaturesScenario.ts](scenarios/createDocumentWithPositionedSignaturesScenario.ts) | `current` | `npm install && cd scenarios/dist && node createDocumentWithPositionedSignaturesScenario.js` |
| Require a CAdES signature on a PDF<br>`create-pdf-with-cades-signature` | Signature formats (CAdES, XAdES) | [createPDFDocumentWithCadesSignatureScenario.ts](scenarios/createPDFDocumentWithCadesSignatureScenario.ts) | `current` | `npm install && cd scenarios/dist && node createPDFDocumentWithCadesSignatureScenario.js` |
| Sign a whole XML file (XAdES)<br>`create-xml-full-signature` | Signature formats (CAdES, XAdES) | [createXmlWithFullDocumentSignatureScenario.ts](scenarios/createXmlWithFullDocumentSignatureScenario.ts) | `current` | `npm install && cd scenarios/dist && node createXmlWithFullDocumentSignatureScenario.js` |
| Sign one element of an XML file (XAdES)<br>`create-xml-element-signature` | Signature formats (CAdES, XAdES) | [createXMLWithElementSignatureScenario.ts](scenarios/createXMLWithElementSignatureScenario.ts) | `current` | `npm install && cd scenarios/dist && node createXMLWithElementSignatureScenario.js` |
| Sign a Brazilian digital diploma (XAdES, Portaria MEC 554/2019)<br>`create-digital-degree` | Signature formats (CAdES, XAdES) | [createDigitalDegreeScenario.ts](scenarios/createDigitalDegreeScenario.ts) | `current` | `npm install && cd scenarios/dist && node createDigitalDegreeScenario.js` |
| Sign with a digital certificate through the API (public signature)<br>`public-signature-with-certificate` | Sign through the API | [publicSignatureScenario.ts](scenarios/publicSignatureScenario.ts) | `current` | `npm install && npx ts-node scenarios/publicSignatureScenario.ts` |
| Sign without leaving your page (embedded signature)<br>`embedded-signature` | Embedded signing | [embeddedSignatureScenario.ts](scenarios/embeddedSignatureScenario.ts) | `current` | `npm install && cd scenarios/dist && node embeddedSignatureScenario.js` |
| Send reminders to participants with pending actions<br>`send-signing-reminders` | Track and list documents | [notifyFlowParticipantsScenario.ts](scenarios/notifyFlowParticipantsScenario.ts) | `current` | `npm install && cd scenarios && node dist/notifyFlowParticipantsScenario.js` |
| List saved document flows<br>`list-document-flows` | Track and list documents | [listDocumentScenario.ts](scenarios/listDocumentScenario.ts) | `current` | `npm install && cd scenarios/dist && node listDocumentScenario.js` |
| Handle document webhooks (signed, approved, refused, concluded)<br>`webhook-document-events` | Webhooks | [checkDocumentStatusScenario.ts](scenarios/checkDocumentStatusScenario.ts) | `current` | `npm install && cd scenarios/dist && node checkDocumentStatusScenario.js` |
| Download a document's versions<br>`download-document-versions` | Download documents | [downloadDocumentVersionScenario.ts](scenarios/downloadDocumentVersionScenario.ts) | `current` | `npm install && cd scenarios && node dist/downloadDocumentVersionScenario.js` |
| Cancel a document<br>`cancel-document` | Document lifecycle | [cancelDocumentScenario.ts](scenarios/cancelDocumentScenario.ts) | `current` | `npm install && cd scenarios && node dist/cancelDocumentScenario.js` |
| Refuse a document as the organization<br>`refuse-document` | Document lifecycle | [refuseDocumentScenario.ts](scenarios/refuseDocumentScenario.ts) | `current` | `npm install && cd scenarios && node dist/refuseDocumentScenario.js` |
| Delete a document<br>`delete-document` | Document lifecycle | [deleteDocumentScenario.ts](scenarios/deleteDocumentScenario.ts) | `current` | `npm install && cd scenarios && node dist/deleteDocumentScenario.js` |
| Upload a new version of a document<br>`add-document-version` | Document lifecycle | [addNewDocumentVersionScenario.ts](scenarios/addNewDocumentVersionScenario.ts) | `current` | `npm install && cd scenarios && node dist/addNewDocumentVersionScenario.js` |
| Create a document in an existing folder<br>`create-document-in-existing-folder` | Folders | [createDocumentInExistingFolderScenario.ts](scenarios/createDocumentInExistingFolderScenario.ts) | `current` | `npm install && cd scenarios/dist && node createDocumentInExistingFolderScenario.js` |
<!-- END GENERATED SAMPLES LIST -->

## Which folder to run from

The scenarios open their input files relative to the working directory, so the folder matters:

- Most scenarios: from `scenarios/dist`, run `node <scenario>.js`, as described above.
- The six that call the shared `CreateDocument()` helper (add version, cancel, delete, download,
  notify, refuse): from `scenarios`, run `node dist/<scenario>.js`. This works only on Windows,
  because the helper's path uses backslashes.
- `publicSignatureScenario.ts` is not compiled into `dist`. From this folder, run
  `npx ts-node scenarios/publicSignatureScenario.ts`.

The table's Command column has the exact command for each scenario. `scenarios/scenario.ts` and
`scenarios/dist/scenario.js` differ (the `.ts` names a `bigSample.pdf` that is not in the
repository), so check the helper before regenerating `dist` with `tsc`. Every run creates real
documents on the configured Signer instance, and Signer e-mails the sample participants (made-up
`@mailinator.com` addresses).

## Configuration

The Signer instance URL and API key are hard-coded in [`scenarios/scenario.ts`](scenarios/scenario.ts)
(`basePath` and `apiKey` in the exported `config`), and again in the compiled
`scenarios/dist/scenario.js`. They point to Lacuna's homologation (test) instance,
`https://signer-lac.azurewebsites.net`, with a shared sample key. To use your own organization,
replace both values with your instance URL and an API key from that same instance.
[`.env.example`](.env.example) lists every value the project uses; it is documentation only,
because nothing loads it.

Prerequisites of some scenarios:

- `createDocumentInExistingFolderScenario`: the organization needs a folder that matches
  "Sample folder".
- `publicSignatureScenario`: uses the Lacuna test certificate
  [`samples/56072386105.pfx`](samples/56072386105.pfx) (password `1234`).

## Receive webhooks locally

`handleWebhook` in [`checkDocumentStatusScenario.ts`](scenarios/checkDocumentStatusScenario.ts)
is not exported or called, and it wraps its argument in `{ data }`, so `type` is always
undefined. Use it only as a list of the event types and models. To receive webhooks:

1. In your own web app (Express, for example), add a `POST` endpoint that parses the JSON body,
   which is already `{ "type": "...", "data": { ... } }`, answers 200 at once, and switches on
   `body.type`.
2. Expose that endpoint with an HTTPS tunnel (ngrok, Cloudflare Tunnel, ...), because Signer calls
   it from its own servers.
3. Register the public URL in Signer under **Organization → Integration**, with an auth type
   other than `NoAuth`, and check that header in your endpoint.

To test without Signer, post a body copied from the
[webhooks docs](https://docs.lacunasoftware.com/en-us/articles/signer/webhooks) to your local
endpoint.
