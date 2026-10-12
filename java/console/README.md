# Signer samples: Java console

A Java 8 console app with one class per Lacuna Signer integration task, in
[`src/main/java/com/lacunasoftware/signer/sample/scenarios`](src/main/java/com/lacunasoftware/signer/sample/scenarios).
Each scenario calls the Signer API through the `com.lacunasoftware.signer:signer-client` Maven
package (2.13.0 in [`build.gradle`](build.gradle)).

The table is generated from the repository's catalog ([samples.json](../../samples.json), also
shown as [SAMPLES.md](../../SAMPLES.md)), where each slug has its docs link and the same task in
other languages. Before changing anything, read [AGENTS.md](../../AGENTS.md).

<!-- BEGIN GENERATED SAMPLES LIST (project: java/console) -->
| Sample | Area | Entry file | Status |
|---|---|---|---|
| Sign a document: create it with one signer<br>`sign-a-document` | Create documents | [CreateDocumentWithOneSignerScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/CreateDocumentWithOneSignerScenario.java) | `current` |
| Create a document with an attachment<br>`create-document-with-attachment` | Create documents | [CreateDocumentWithAttachmentScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/CreateDocumentWithAttachmentScenario.java) | `current` |
| Create a document with a description<br>`create-document-with-description` | Create documents | [CreateDocumentWithDescriptionScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/CreateDocumentWithDescriptionScenario.java) | `current` |
| Merge several PDFs into one document (envelope)<br>`create-envelope` | Create documents | [CreateDocumentWithEnvelopeScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/CreateDocumentWithEnvelopeScenario.java) | `current` |
| Generate a document from a PDF form template and CSV data<br>`generate-document-from-template` | Create documents | [GenerateDocumentWithTwoOrMoreSignersScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/GenerateDocumentWithTwoOrMoreSignersScenario.java) | `current` |
| Create a document with two or more signers, in any order<br>`create-document-multiple-signers` | Flows: participants, order and rules | [CreateDocumentWithTwoOrMoreSignersWithoutOrderScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/CreateDocumentWithTwoOrMoreSignersWithoutOrderScenario.java) | `current` |
| Create a document with signers in a fixed order<br>`create-document-ordered-flow` | Flows: participants, order and rules | [CreateDocumentWithTwoOrMoreSignersWithOrderScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/CreateDocumentWithTwoOrMoreSignersWithOrderScenario.java) | `current` |
| Create a document with an approver<br>`create-document-with-approver` | Flows: participants, order and rules | [CreateDocumentWithApproversScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/CreateDocumentWithApproversScenario.java) | `current` |
| Create a document with a signing rule (any N of a group)<br>`create-document-with-signing-rule` | Flows: participants, order and rules | [CreateDocumentWithSigningRuleScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/CreateDocumentWithSigningRuleScenario.java) | `current` |
| Pre-position a signature on the document<br>`create-document-with-positioned-signatures` | Flows: participants, order and rules | [CreateDocumentWithPositionedSignaturesScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/CreateDocumentWithPositionedSignaturesScenario.java) | `current` |
| Require a CAdES signature on a PDF<br>`create-pdf-with-cades-signature` | Signature formats (CAdES, XAdES) | [CreatePDFDocumentWithCadesSignatureScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/CreatePDFDocumentWithCadesSignatureScenario.java) | `current` |
| Sign a whole XML file (XAdES)<br>`create-xml-full-signature` | Signature formats (CAdES, XAdES) | [CreateXmlWithFullDocumentSignatureScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/CreateXmlWithFullDocumentSignatureScenario.java) | `current` |
| Sign one element of an XML file (XAdES)<br>`create-xml-element-signature` | Signature formats (CAdES, XAdES) | [CreateXMLWithElementSignatureScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/CreateXMLWithElementSignatureScenario.java) | `current` |
| Sign a Brazilian digital diploma (XAdES, Portaria MEC 554/2019)<br>`create-digital-degree` | Signature formats (CAdES, XAdES) | [CreateDigitalDegreeScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/CreateDigitalDegreeScenario.java) | `current` |
| Sign with a digital certificate through the API (public signature)<br>`public-signature-with-certificate` | Sign through the API | [PublicSignatureScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/PublicSignatureScenario.java) | `current` |
| Sign electronically with an uploaded signature image<br>`electronic-signature-with-signature-image` | Sign through the API | [ElectronicSignatureWithSignatureImageScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/ElectronicSignatureWithSignatureImageScenario.java) | `current` |
| Sign without leaving your page (embedded signature)<br>`embedded-signature` | Embedded signing | [EmbeddedSignatureScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/EmbeddedSignatureScenario.java) | `current` |
| Check a document's status and its participants' actions<br>`check-document-status` | Track and list documents | [CheckDocumentStatusScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/CheckDocumentStatusScenario.java) | `current` |
| Send reminders to participants with pending actions<br>`send-signing-reminders` | Track and list documents | [NotifyFlowParticipantsScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/NotifyFlowParticipantsScenario.java) | `current` |
| List documents<br>`list-documents` | Track and list documents | [ListDocumentsScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/ListDocumentsScenario.java) | `current` |
| Handle document webhooks (signed, approved, refused, concluded)<br>`webhook-document-events` | Webhooks | [CheckDocumentStatusScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/CheckDocumentStatusScenario.java) | `current` |
| Handle the invoice-closed billing webhook<br>`webhook-invoice-closed` | Webhooks | [InvoiceWebhookHandlingScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/InvoiceWebhookHandlingScenario.java) | `current` |
| Mark an invoice as paid<br>`mark-invoice-paid` | Billing | [InvoiceWebhookHandlingScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/InvoiceWebhookHandlingScenario.java) | `current` |
| Download a document's versions<br>`download-document-versions` | Download documents | [DownloadDocumentVersionScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/DownloadDocumentVersionScenario.java) | `current` |
| Cancel a document<br>`cancel-document` | Document lifecycle | [CancelDocumentScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/CancelDocumentScenario.java) | `current` |
| Refuse a document as the organization<br>`refuse-document` | Document lifecycle | [RefuseDocumentScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/RefuseDocumentScenario.java) | `current` |
| Delete a document<br>`delete-document` | Document lifecycle | [DeleteDocumentScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/DeleteDocumentScenario.java) | `current` |
| Upload a new version of a document<br>`add-document-version` | Document lifecycle | [AddNewDocumentVersionScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/AddNewDocumentVersionScenario.java) | `current` |
| Create a document in a new folder<br>`create-document-in-new-folder` | Folders | [CreateDocumentInNewFolderScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/CreateDocumentInNewFolderScenario.java) | `current` |
| Create a document in an existing folder<br>`create-document-in-existing-folder` | Folders | [CreateDocumentInExistingFolder.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/CreateDocumentInExistingFolder.java) | `current` |
<!-- END GENERATED SAMPLES LIST -->

## Run a scenario

1. In [`App.java`](src/main/java/com/lacunasoftware/signer/sample/App.java), set the scenario to
   run: `Scenario scenario = new CreateDocumentWithOneSignerScenario();`
2. From this folder, run:

   ```bash
   ./gradlew run
   ```

   On Windows, use `gradlew.bat run`. The wrapper is Gradle 5.2.1, which runs on JDK 8 to 11.

Use Gradle: `pom.xml` still references `signer-client` 1.0.0 and a Bintray repository that no
longer exists. Every run creates real documents on the configured Signer instance, and Signer
e-mails the sample participants (made-up `@mailinator.com` addresses).

## Configuration

The Signer instance URL and API key are hard-coded in `Init()` of
[`scenarios/Scenario.java`](src/main/java/com/lacunasoftware/signer/sample/scenarios/Scenario.java)
(`domain` and `token`). They point to the Signer sandbox, `https://signer-lac.azurewebsites.net`,
with a shared sample key. The sandbox is where you should develop and test: to use your own
organization there, replace the key with one of yours. For production, change the URL and the key
together. [`.env.example`](.env.example) lists every value the project uses; it is documentation
only, because nothing loads it. Input files live in
[`src/main/resources/static`](src/main/resources/static).

Prerequisites of some scenarios:

- `CreateDocumentInExistingFolder`: the organization needs a folder named "Sample Folder".
- `PublicSignatureScenario`: needs `certificate.cer` in `src/main/resources/static`, and its
  `performSignature` method is a stub that throws. Use the .NET or Node.js version as the
  reference.
- `InvoiceWebhookHandlingScenario.Run`: needs an API key with billing administration privileges
  and a real invoice id.

## Receive webhooks locally

`CheckDocumentStatusScenario.HandleWebhook` (document events) and
`InvoiceWebhookHandlingScenario.HandleWebhook` (`InvoiceClosed`) take a `WebhookModel`, but
nothing calls them: this console app has no HTTP endpoint. To try one:

1. In your own web app (Spring Boot, for example), add a `POST` endpoint that reads the JSON body
   (`{ "type": "...", "data": { ... } }`) into `WebhookModel` with `signerClient.getGson()`,
   answers 200 at once, and passes the model to the handler. Call `Init()` first:
   `CheckDocumentStatusScenario.HandleWebhook` uses `signerClient`.
2. Expose that endpoint with an HTTPS tunnel (ngrok, Cloudflare Tunnel, ...), because Signer calls
   it from its own servers.
3. Register the public URL in Signer under **Organization → Integration**, with an auth type
   other than `NoAuth`, and check that header in your endpoint.

To test without Signer, post a body copied from the
[webhooks docs](https://docs.lacunasoftware.com/en-us/articles/signer/webhooks) to your local
endpoint. `InvoiceClosed` is sent by the instance's billing webhook, which the system
administrator configures, not by an organization webhook.
