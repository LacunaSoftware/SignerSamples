# Signer samples: .NET console

A .NET 10 console app with one class per Lacuna Signer integration task, in
[`Console/Scenarios`](Console/Scenarios). Each scenario calls the Signer API through the
`Lacuna.Signer.Client` NuGet package (1.24.0, with `Lacuna.Signer.Api` 1.77.1).

The table is generated from the repository's catalog ([samples.json](../../samples.json), also
shown as [SAMPLES.md](../../SAMPLES.md)), where each slug has its docs link and the same task in
other languages. Before changing anything, read [AGENTS.md](../../AGENTS.md).

<!-- BEGIN GENERATED SAMPLES LIST (project: dotnet/console) -->
| Sample | Area | Entry file | Status |
|---|---|---|---|
| Sign a document: create it with one signer<br>`sign-a-document` | Create documents | [CreateDocumentWithOneSignerScenario.cs](Console/Scenarios/CreateDocumentWithOneSignerScenario.cs) | `current` |
| Create a document with an attachment<br>`create-document-with-attachment` | Create documents | [CreateDocumentWithAttachmentScenario.cs](Console/Scenarios/CreateDocumentWithAttachmentScenario.cs) | `current` |
| Create a document with a description<br>`create-document-with-description` | Create documents | [CreateDocumentWithDescriptionScenario.cs](Console/Scenarios/CreateDocumentWithDescriptionScenario.cs) | `current` |
| Merge several PDFs into one document (envelope)<br>`create-envelope` | Create documents | [CreateDocumentWithEnvelopeScenario.cs](Console/Scenarios/CreateDocumentWithEnvelopeScenario.cs) | `current` |
| Create a document with two or more signers, in any order<br>`create-document-multiple-signers` | Flows: participants, order and rules | [CreateDocumentWithTwoOrMoreSignersWithoutOrderScenario.cs](Console/Scenarios/CreateDocumentWithTwoOrMoreSignersWithoutOrderScenario.cs) | `current` |
| Create a document with signers in a fixed order<br>`create-document-ordered-flow` | Flows: participants, order and rules | [CreateDocumentWithTwoOrMoreSignersWithOrderScenario.cs](Console/Scenarios/CreateDocumentWithTwoOrMoreSignersWithOrderScenario.cs) | `current` |
| Create a document with an approver<br>`create-document-with-approver` | Flows: participants, order and rules | [CreateDocumentWithApproversScenario.cs](Console/Scenarios/CreateDocumentWithApproversScenario.cs) | `current` |
| Create a document with a signing rule (any N of a group)<br>`create-document-with-signing-rule` | Flows: participants, order and rules | [CreateDocumentWithSigningRuleScenario.cs](Console/Scenarios/CreateDocumentWithSigningRuleScenario.cs) | `current` |
| Pre-position a signature on the document<br>`create-document-with-positioned-signatures` | Flows: participants, order and rules | [CreateDocumentWithPositionedSignaturesScenario.cs](Console/Scenarios/CreateDocumentWithPositionedSignaturesScenario.cs) | `current` |
| Require a CAdES signature on a PDF<br>`create-pdf-with-cades-signature` | Signature formats (CAdES, XAdES) | [CreatePDFDocumentWithCadesSignatureScenario.cs](Console/Scenarios/CreatePDFDocumentWithCadesSignatureScenario.cs) | `current` |
| Sign a whole XML file (XAdES)<br>`create-xml-full-signature` | Signature formats (CAdES, XAdES) | [CreateXMLWithFullDocumentSignatureScenario.cs](Console/Scenarios/CreateXMLWithFullDocumentSignatureScenario.cs) | `current` |
| Sign one element of an XML file (XAdES)<br>`create-xml-element-signature` | Signature formats (CAdES, XAdES) | [CreateXMLWithElementSignatureScenario.cs](Console/Scenarios/CreateXMLWithElementSignatureScenario.cs) | `current` |
| Sign a Brazilian digital diploma (XAdES, Portaria MEC 554/2019)<br>`create-digital-degree` | Signature formats (CAdES, XAdES) | [CreateDigitalDegreeScenario.cs](Console/Scenarios/CreateDigitalDegreeScenario.cs) | `current` |
| Sign with a digital certificate through the API (public signature)<br>`public-signature-with-certificate` | Sign through the API | [PublicSignatureScenario.cs](Console/Scenarios/PublicSignatureScenario.cs) | `current` |
| Sign without leaving your page (embedded signature)<br>`embedded-signature` | Embedded signing | [EmbeddedSignatureScenario.cs](Console/Scenarios/EmbeddedSignatureScenario.cs) | `current` |
| Check a document's status and its participants' actions<br>`check-document-status` | Track and list documents | [CheckDocumentStatusScenario.cs](Console/Scenarios/CheckDocumentStatusScenario.cs) | `current` |
| Send reminders to participants with pending actions<br>`send-signing-reminders` | Track and list documents | [NotifyFlowParticipantsScenario.cs](Console/Scenarios/NotifyFlowParticipantsScenario.cs) | `current` |
| List documents<br>`list-documents` | Track and list documents | [ListDocumentsScenario.cs](Console/Scenarios/ListDocumentsScenario.cs) | `current` |
| Receive webhooks with a minimal HTTP endpoint<br>`webhook-endpoint` | Webhooks | [WebhookEndpointScenario.cs](Console/Scenarios/WebhookEndpointScenario.cs) | `current` |
| Handle document webhooks (signed, approved, refused, concluded)<br>`webhook-document-events` | Webhooks | [CheckDocumentStatusScenario.cs](Console/Scenarios/CheckDocumentStatusScenario.cs) | `current` |
| Handle the invoice-closed billing webhook<br>`webhook-invoice-closed` | Webhooks | [InvoiceWebhookHandlingScenario.cs](Console/Scenarios/InvoiceWebhookHandlingScenario.cs) | `current` |
| Mark an invoice as paid<br>`mark-invoice-paid` | Billing | [InvoiceWebhookHandlingScenario.cs](Console/Scenarios/InvoiceWebhookHandlingScenario.cs) | `current` |
| Download a document's versions<br>`download-document-versions` | Download documents | [DownloadDocumentVersionScenario.cs](Console/Scenarios/DownloadDocumentVersionScenario.cs) | `current` |
| Cancel a document<br>`cancel-document` | Document lifecycle | [CancelDocumentScenario.cs](Console/Scenarios/CancelDocumentScenario.cs) | `current` |
| Refuse a document as the organization<br>`refuse-document` | Document lifecycle | [RefuseDocumentScenario.cs](Console/Scenarios/RefuseDocumentScenario.cs) | `current` |
| Delete a document<br>`delete-document` | Document lifecycle | [DeleteDocumentScenario.cs](Console/Scenarios/DeleteDocumentScenario.cs) | `current` |
| Upload a new version of a document<br>`add-document-version` | Document lifecycle | [AddNewDocumentVersionScenario.cs](Console/Scenarios/AddNewDocumentVersionScenario.cs) | `current` |
| Create a document in a new folder<br>`create-document-in-new-folder` | Folders | [CreateDocumentInNewFolderScenario.cs](Console/Scenarios/CreateDocumentInNewFolderScenario.cs) | `current` |
| Create a document in an existing folder<br>`create-document-in-existing-folder` | Folders | [CreateDocumentInExistingFolderScenario.cs](Console/Scenarios/CreateDocumentInExistingFolderScenario.cs) | `current` |
<!-- END GENERATED SAMPLES LIST -->

## Run a scenario

You need the [.NET 10 SDK](https://dotnet.microsoft.com/download/dotnet/10.0).

1. In [`Console/Program.cs`](Console/Program.cs), set the scenario to run:
   `var scenario = new CreateDocumentWithOneSignerScenario();`
2. From this folder, run `dotnet run --project Console`. Or open `Console.sln` in Visual Studio
   2026 (or another IDE with .NET 10 support) and press F5.

Both start the program in its output folder, `Console/bin/Debug/net10.0`, where the build copies
the input files (`sample.pdf`, `sample.xml`, `sample-degree.xml`).

Every run creates real documents on the configured Signer instance, and Signer e-mails the sample
participants (made-up `@mailinator.com` addresses).

## Configuration

The constructor of [`Console/Scenarios/Scenario.cs`](Console/Scenarios/Scenario.cs) reads the
Signer instance URL from the `SIGNER_ENDPOINT` environment variable and the API key from
`SIGNER_API_KEY`. When a variable is unset or empty, it falls back to the Signer sandbox,
`https://signer-lac.azurewebsites.net`, and a shared sample key, so the samples run as they are.
The sandbox is where you should develop and test: to use your own organization there, set
`SIGNER_API_KEY` to one of your keys. For production, set both variables together.
[`.env.example`](.env.example) lists every value the project uses, including the webhook
endpoint's; nothing loads that file automatically.

The program inherits the variables from whatever starts it. Set them for one shell and run it
from there. In PowerShell:

```powershell
$env:SIGNER_ENDPOINT = "https://signer-lac.azurewebsites.net"
$env:SIGNER_API_KEY = "your-application|your-key"
dotnet run --project Console
```

In bash (Git Bash on Windows), quoting the key because of its `|`:

```bash
export SIGNER_ENDPOINT=https://signer-lac.azurewebsites.net
export SIGNER_API_KEY='your-application|your-key'
dotnet run --project Console
```

For Visual Studio, set them for your Windows user and then restart Visual Studio, from PowerShell:

```powershell
[Environment]::SetEnvironmentVariable("SIGNER_API_KEY", "your-application|your-key", "User")
```

Prerequisites of some scenarios:

- `CreateDocumentInExistingFolderScenario`: the organization needs a folder named "Sample Folder".
- `PublicSignatureScenario`: copy [`nodejs/samples/56072386105.pfx`](../../nodejs/samples/56072386105.pfx)
  (a Lacuna test certificate, password `1234`) next to `Console.exe`, in
  `Console/bin/Debug/net10.0`.
- `InvoiceWebhookHandlingScenario.RunAsync`: needs an API key with billing administration
  privileges and a real invoice id.

## Tests

[`Console.Tests`](Console.Tests) is an xUnit v3 project that runs the scenarios unchanged. Its
fake Signer, an ASP.NET Core server on `127.0.0.1`, takes the place of the real one through
`SIGNER_ENDPOINT`, records every request and answers with the client's own models; a route it
doesn't know answers 404, so a scenario that calls something new fails its test. The tests check
what each scenario sends (files, flow actions, steps, XAdES options, query filters), the
download and public-signature steps (the signature is verified with the test certificate), the
`HandleWebhook` methods, and the webhook endpoint over HTTP (401, 400, repeated events,
`DocumentConcluded` read back). They need no API key and send no e-mail. From this folder:

```bash
dotnet test --solution Console.sln
```

In Visual Studio, use Test Explorer. CI runs them on every pull request that touches this folder.

`LiveSandboxTests` run most scenarios against a real Signer instead, with `SIGNER_ENDPOINT` and
`SIGNER_API_KEY` or the sandbox defaults. They are skipped unless `SIGNER_LIVE_TESTS` is `1`,
because each run creates documents and Signer e-mails the sample participants:

```bash
SIGNER_LIVE_TESTS=1 dotnet test --solution Console.sln
```

## Receive webhooks locally

[`WebhookEndpointScenario`](Console/Scenarios/WebhookEndpointScenario.cs) is a minimal webhook
endpoint built on `System.Net.HttpListener`. It listens on `http://localhost:8080/webhook` (set
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

It then passes the event to `CheckDocumentStatusScenario.HandleWebhook`, which switches on `type`.

To test it without Signer, set `var scenario = new WebhookEndpointScenario();` in `Program.cs`,
run it (`dotnet run --project Console`), and post a `DocumentSigned` event with the default token from bash (Git Bash on
Windows):

```bash
curl -i http://localhost:8080/webhook \
  -H "Authorization: Bearer my-webhook-token" \
  -H "Content-Type: application/json" \
  -d '{"type":"DocumentSigned","data":{"id":"3fa85f64-5717-4562-b3fc-2c963f66afa6","name":"sample.pdf",
       "signature":{"flowActionId":"5c1a2b3c-4d5e-4f60-8a7b-9c0d1e2f3a4b","name":"Jack Bauer",
       "identifier":"75502846369","emailAddress":"jack.bauer@mailinator.com"}}}'
```

The answer is `200`, and the console prints `Accepted: ...` and `Document ... is signed!`. Post it
again and the console prints `Ignored a repeated event`; change the token and the answer is `401`.
A `DocumentConcluded` body, `{"type":"DocumentConcluded","data":{"id":"<document id>"}}`, makes
the endpoint read that document from Signer after answering; with a made-up id, that call fails.

To receive real events:

1. Expose port 8080 with an HTTPS tunnel, because Signer calls the endpoint from its own servers.
   `HttpListener` answers 400 to any Host header other than `localhost`, so have the tunnel
   rewrite it, for example `ngrok http 8080 --host-header=rewrite`.
2. In Signer, under **Organization → Integration**, register `https://<tunnel host>/webhook` with
   the auth type `Bearer` and a long random token, or call
   `POST /api/organizations/{orgId}/webhooks` with `url`, `authType` and `token`. Set
   `SIGNER_WEBHOOK_TOKEN` to the same token (see Configuration).
3. Webhooks belong to an organization (personal organizations have none), so register it in your
   own organization and create the documents there, with its API key (see Configuration).

`InvoiceClosed` is sent by the instance's billing webhook, which the system administrator
configures, not by an organization webhook. `InvoiceWebhookHandlingScenario.HandleWebhook` handles
it, but no endpoint calls it.
