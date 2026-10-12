# Signer samples: .NET console

A .NET Framework 4.8 console app with one class per Lacuna Signer integration task, in
[`Console/Scenarios`](Console/Scenarios). Each scenario calls the Signer API through the
`Lacuna.Signer.Client` NuGet package (1.17.0, with `Lacuna.Signer.Api` 1.47.0).

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

1. Open `Console.sln` in Visual Studio, with the .NET Framework 4.8 targeting pack installed,
   and let it restore the NuGet packages (`packages.config`).
2. In [`Console/Program.cs`](Console/Program.cs), set the scenario to run:
   `var scenario = new CreateDocumentWithOneSignerScenario();`
3. Press F5.

Every run creates real documents on the configured Signer instance, and Signer e-mails the sample
participants (made-up `@mailinator.com` addresses).

## Configuration

The Signer instance URL and API key are hard-coded in the constructor of
[`Console/Scenarios/Scenario.cs`](Console/Scenarios/Scenario.cs) (`domain` and `token`). They
point to the Signer sandbox, `https://signer-lac.azurewebsites.net`, with a shared sample key.
The sandbox is where you should develop and test: to use your own organization there, replace the
key with one of yours. For production, change the URL and the key together.
[`.env.example`](.env.example) lists every value the project uses; it is documentation only,
because nothing loads it.

Prerequisites of some scenarios:

- `CreateDocumentInExistingFolderScenario`: the organization needs a folder named "Sample Folder".
- `PublicSignatureScenario`: copy [`nodejs/samples/56072386105.pfx`](../../nodejs/samples/56072386105.pfx)
  (a Lacuna test certificate, password `1234`) next to `Console.exe`, for example to
  `Console/bin/Debug`.
- `AddNewDocumentVersionScenario`: the file is not listed in `Console.csproj`, so add it to the
  project before selecting it.
- `InvoiceWebhookHandlingScenario.RunAsync`: needs an API key with billing administration
  privileges and a real invoice id.

## Receive webhooks locally

`CheckDocumentStatusScenario.HandleWebhook` (document events) and
`InvoiceWebhookHandlingScenario.HandleWebhook` (`InvoiceClosed`) take a `WebhookModel`, but
nothing calls them: this console app has no HTTP endpoint. To try one:

1. In your own web app (ASP.NET Core, for example), add a `POST` endpoint that reads the JSON body
   (`{ "type": "...", "data": { ... } }`) into `WebhookModel`, answers 200 at once, and passes the
   model to the handler.
2. Expose that endpoint with an HTTPS tunnel (ngrok, Cloudflare Tunnel, Visual Studio dev tunnels,
   ...), because Signer calls it from its own servers.
3. Register the public URL in Signer under **Organization → Integration**, with an auth type
   other than `NoAuth`, and check that header in your endpoint.

To test without Signer, post a body copied from the
[webhooks docs](https://docs.lacunasoftware.com/en-us/articles/signer/webhooks) to your local
endpoint. `InvoiceClosed` is sent by the instance's billing webhook, which the system
administrator configures, not by an organization webhook.
