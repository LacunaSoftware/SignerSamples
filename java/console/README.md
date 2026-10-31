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
| Choose the language of notification e-mails<br>`create-document-with-notification-language` | Create documents | [CreateDocumentWithNotificationLanguageScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/CreateDocumentWithNotificationLanguageScenario.java) | `current` |
| Merge several PDFs into one document (envelope)<br>`create-envelope` | Create documents | [CreateDocumentWithEnvelopeScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/CreateDocumentWithEnvelopeScenario.java) | `current` |
| Generate a document from a PDF form template and CSV data<br>`generate-document-from-template` | Create documents | [GenerateDocumentWithTwoOrMoreSignersScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/GenerateDocumentWithTwoOrMoreSignersScenario.java) | `current` |
| Create a document with two or more signers, in any order<br>`create-document-multiple-signers` | Flows: participants, order and rules | [CreateDocumentWithTwoOrMoreSignersWithoutOrderScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/CreateDocumentWithTwoOrMoreSignersWithoutOrderScenario.java) | `current` |
| Create a document with signers in a fixed order<br>`create-document-ordered-flow` | Flows: participants, order and rules | [CreateDocumentWithTwoOrMoreSignersWithOrderScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/CreateDocumentWithTwoOrMoreSignersWithOrderScenario.java) | `current` |
| Create a document with an approver<br>`create-document-with-approver` | Flows: participants, order and rules | [CreateDocumentWithApproversScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/CreateDocumentWithApproversScenario.java) | `current` |
| Create a document with a signing rule (any N of a group)<br>`create-document-with-signing-rule` | Flows: participants, order and rules | [CreateDocumentWithSigningRuleScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/CreateDocumentWithSigningRuleScenario.java) | `current` |
| Pre-position a signature on the document<br>`create-document-with-positioned-signatures` | Flows: participants, order and rules | [CreateDocumentWithPositionedSignaturesScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/CreateDocumentWithPositionedSignaturesScenario.java) | `current` |
| Create a document a signer cannot refuse<br>`create-document-without-refusal` | Flows: participants, order and rules | [CreateDocumentWithoutRefusalScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/CreateDocumentWithoutRefusalScenario.java) | `current` |
| Control who positions the signature<br>`create-document-with-signer-positioning` | Flows: participants, order and rules | [CreateDocumentWithSignerPositioningScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/CreateDocumentWithSignerPositioningScenario.java) | `current` |
| Require a CAdES signature on a PDF<br>`create-pdf-with-cades-signature` | Signature formats (CAdES, XAdES) | [CreatePDFDocumentWithCadesSignatureScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/CreatePDFDocumentWithCadesSignatureScenario.java) | `current` |
| Convert to PDF/A and timestamp the signatures<br>`create-pdfa-document-with-timestamp` | Signature formats (CAdES, XAdES) | [CreatePdfADocumentWithTimestampScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/CreatePdfADocumentWithTimestampScenario.java) | `current` |
| Sign a whole XML file (XAdES)<br>`create-xml-full-signature` | Signature formats (CAdES, XAdES) | [CreateXmlWithFullDocumentSignatureScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/CreateXmlWithFullDocumentSignatureScenario.java) | `current` |
| Sign one element of an XML file (XAdES)<br>`create-xml-element-signature` | Signature formats (CAdES, XAdES) | [CreateXMLWithElementSignatureScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/CreateXMLWithElementSignatureScenario.java) | `current` |
| Sign a Brazilian digital diploma (XAdES, Portaria MEC 554/2019)<br>`create-digital-degree` | Signature formats (CAdES, XAdES) | [CreateDigitalDegreeScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/CreateDigitalDegreeScenario.java) | `current` |
| Sign with a digital certificate through the API (public signature)<br>`public-signature-with-certificate` | Sign through the API | [PublicSignatureScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/PublicSignatureScenario.java) | `deprecated` |
| Sign electronically with an uploaded signature image<br>`electronic-signature-with-signature-image` | Sign through the API | [ElectronicSignatureWithSignatureImageScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/ElectronicSignatureWithSignatureImageScenario.java) | `current` |
| Sign without leaving your page (embedded signature)<br>`embedded-signature` | Embedded signing | [EmbeddedSignatureScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/EmbeddedSignatureScenario.java) | `current` |
| Check a document's status and its participants' actions<br>`check-document-status` | Track and list documents | [CheckDocumentStatusScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/CheckDocumentStatusScenario.java) | `current` |
| Send reminders to participants with pending actions<br>`send-signing-reminders` | Track and list documents | [NotifyFlowParticipantsScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/NotifyFlowParticipantsScenario.java) | `current` |
| List documents<br>`list-documents` | Track and list documents | [ListDocumentsScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/ListDocumentsScenario.java) | `current` |
| Receive webhooks with a minimal HTTP endpoint<br>`webhook-endpoint` | Webhooks | [WebhookEndpointScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/WebhookEndpointScenario.java) | `current` |
| Handle document webhooks (signed, approved, refused, concluded)<br>`webhook-document-events` | Webhooks | [CheckDocumentStatusScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/CheckDocumentStatusScenario.java) | `current` |
| Handle the invoice-closed billing webhook<br>`webhook-invoice-closed` | Webhooks | [InvoiceWebhookHandlingScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/InvoiceWebhookHandlingScenario.java) | `current` |
| Mark an invoice as paid<br>`mark-invoice-paid` | Billing | [InvoiceWebhookHandlingScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/InvoiceWebhookHandlingScenario.java) | `current` |
| Download a document's versions<br>`download-document-versions` | Download documents | [DownloadDocumentVersionScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/DownloadDocumentVersionScenario.java) | `current` |
| Cancel a document<br>`cancel-document` | Document lifecycle | [CancelDocumentScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/CancelDocumentScenario.java) | `current` |
| Refuse a document as the organization<br>`refuse-document` | Document lifecycle | [RefuseDocumentScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/RefuseDocumentScenario.java) | `current` |
| Pause and resume a document<br>`pause-resume-document` | Document lifecycle | [PauseAndResumeDocumentScenario.java](src/main/java/com/lacunasoftware/signer/sample/scenarios/PauseAndResumeDocumentScenario.java) | `current` |
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

Gradle is the only supported build; there is no Maven build. Every run creates real documents on
the configured Signer instance, and Signer e-mails the sample participants (made-up
`@mailinator.com` addresses).

## Configuration

`Init()` of
[`scenarios/Scenario.java`](src/main/java/com/lacunasoftware/signer/sample/scenarios/Scenario.java)
reads the Signer instance URL from the `SIGNER_ENDPOINT` environment variable and the API key from
`SIGNER_API_KEY`. When a variable is unset or empty, it falls back to the Signer sandbox,
`https://signer-lac.azurewebsites.net`, and a shared sample key, so the samples run as they are.
The sandbox is where you should develop and test: to use your own organization there, set
`SIGNER_API_KEY` to one of your keys. For production, set both variables together.
[`.env.example`](.env.example) lists every value the project uses, including the webhook
endpoint's; nothing loads that file automatically. Input files live in
[`src/main/resources/static`](src/main/resources/static).

Set the variables in the shell that runs Gradle, which passes them on to the program. In bash,
quoting the key because of its `|`:

```bash
export SIGNER_ENDPOINT=https://signer-lac.azurewebsites.net
export SIGNER_API_KEY='your-application|your-key'
./gradlew run
```

In PowerShell:

```powershell
$env:SIGNER_ENDPOINT = "https://signer-lac.azurewebsites.net"
$env:SIGNER_API_KEY = "your-application|your-key"
.\gradlew.bat run
```

Prerequisites of some scenarios:

- `CreateDocumentInExistingFolder`: the organization needs a folder named "Sample Folder".
- `PublicSignatureScenario`: needs `certificate.cer` in `src/main/resources/static`, and its
  `performSignature` method is a stub that throws. Use the .NET or Node.js version as the
  reference.
- `InvoiceWebhookHandlingScenario.Run`: needs an API key with billing administration privileges
  and a real invoice id.

## Receive webhooks locally

[`WebhookEndpointScenario`](src/main/java/com/lacunasoftware/signer/sample/scenarios/WebhookEndpointScenario.java)
is a minimal webhook endpoint built on the JDK's `com.sun.net.httpserver.HttpServer`. It listens on
`http://localhost:8080/webhook` (set `SIGNER_WEBHOOK_PORT` for another port) and follows the
[webhooks docs](https://docs.lacunasoftware.com/en-us/articles/signer/webhooks#best-practices):

1. It answers 401 unless the `Authorization` header is `Bearer <token>` with the token you
   registered, set in `SIGNER_WEBHOOK_TOKEN` (default `my-webhook-token`), because Signer does not
   sign the body.
2. It answers 200 at once and processes the event afterwards, on another thread. Signer retries a
   non-2xx answer or a connection error, up to 10 times by default.
3. It ignores an event it has already received, keyed on `type`, the document `id` and, when there
   is one, `flowActionId`. It keeps the keys in memory; production needs durable storage.
4. Events can arrive out of order, so before acting on `DocumentConcluded` it reads the document
   back (`GET /api/documents/{id}`) and checks that its `status` is `Concluded`. This is its only
   call to Signer.

It then reads the body into a `WebhookModel` with `signerClient.getGson()`, which handles the
API's enums and dates, and passes it to `CheckDocumentStatusScenario.HandleWebhook`.

To test it without Signer, set `Scenario scenario = new WebhookEndpointScenario();` in `App.java`,
run `./gradlew run`, and post a `DocumentSigned` event with the default token from bash (Git Bash
on Windows):

```bash
curl -i http://localhost:8080/webhook \
  -H "Authorization: Bearer my-webhook-token" \
  -H "Content-Type: application/json" \
  -d '{"type":"DocumentSigned","data":{"id":"3fa85f64-5717-4562-b3fc-2c963f66afa6","name":"sample.pdf",
       "signature":{"flowActionId":"5c1a2b3c-4d5e-4f60-8a7b-9c0d1e2f3a4b","name":"Jack Bauer",
       "identifier":"75502846369","emailAddress":"jack.bauer@mailinator.com"}}}'
```

The answer is `200`, and the console prints `Accepted: ...` and `Document ... is Signed`. Post it
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

`InvoiceClosed` is sent by the instance's billing webhook, which the system administrator
configures, not by an organization webhook. `InvoiceWebhookHandlingScenario.HandleWebhook` handles
it, but no endpoint calls it.

## Tests

[`src/test`](src/test/java/com/lacunasoftware/signer/sample/scenarios) holds JUnit 4 tests of
the base class's `callSignerApi` and `requiredField` (the headers, the JSON body, the errors on a
non-2xx or non-JSON answer) and of the webhook endpoint over HTTP (401, 404, 400, repeated events,
two participants' events, the answer before the processing, `DocumentConcluded` read back, an
event that fails to process). Their fake Signer, on the JDK's HTTP server, takes the place of the
real one through `SIGNER_ENDPOINT`. `build.gradle` sets that variable, the API key and the webhook
port and token for the test run only, so the tests need no API key and send no e-mail. They don't
run the other scenarios yet. From this folder (`gradlew.bat test` on Windows):

```bash
./gradlew test
```

`./gradlew build` compiles every scenario and runs the tests, as CI does on JDK 8 and 11
([`test-java-console.yml`](../../.github/workflows/test-java-console.yml)).
