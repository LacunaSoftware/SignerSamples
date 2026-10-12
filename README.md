# Signer Samples
This repository contains sample projects demonstrating the usage of [Signer](https://www.dropsigner.com/) in various programming languages and frameworks.

- Documentation: [Signer docs (English)](https://docs.lacunasoftware.com/en-us/articles/signer), starting with [Getting started](https://docs.lacunasoftware.com/en-us/articles/signer/get-started). API reference: [Swagger](https://www.dropsigner.com/swagger).
- Live demos: [Sign a document](https://try.lacunasoftware.com/en/signer/sign-a-document) and [Sign without leaving your page](https://try.lacunasoftware.com/en/signer/embedded-signature).

**To get started, choose a programming language:**
- [.NET](dotnet/)
- [Java](java/)
- [PHP](php/)
- [Nodejs](nodejs/)

## Find a sample by task

[SAMPLES.md](SAMPLES.md) lists every sample by feature area and task (create a document, order
the signers, embed the signing page, handle webhooks, download the signed file, ...), with one
column per language and a link to each file. It is generated from [samples.json](samples.json),
which also records each sample's related files, run command, docs link and status:

| Status | Meaning |
|---|---|
| `current` | Recommended for new integrations. |
| `legacy` | Works and is still supported, but prefer the `current` alternative for new code. |
| `deprecated` | Kept for reference only. Do not copy into new code. |

Every sample runs against the Signer sandbox, `https://signer-lac.azurewebsites.net`, with a
shared sample key. Develop and test your own integration against the sandbox too, with your
organization's own key, and switch the URL and key together for production. Each project's
README explains how to run it and where to put your own instance URL and API key.

## Using this repo with an AI coding agent

If you use Claude Code, Cursor, Copilot, Codex or another coding agent, point it at
[AGENTS.md](AGENTS.md) ([CLAUDE.md](CLAUDE.md) imports it for Claude Code). It tells the agent to
look the task up in the catalog ([samples.json](samples.json), [SAMPLES.md](SAMPLES.md)) before
reading code, how to run each project, how the Signer integration works, and the pitfalls the
samples show. The catalog uses the same schema as
[PkiSuiteSamples](https://github.com/LacunaSoftware/PkiSuiteSamples).

## Language coverage

Which tasks each language implements; a ✓ links to the sample and — means there is none yet.
This table is generated from [samples.json](samples.json).

<!-- BEGIN GENERATED GAP MATRIX -->
| Area | Task | .NET | Java | Node.js | PHP |
|---|---|:-:|:-:|:-:|:-:|
| Create documents | Sign a document: create it with one signer<br>`sign-a-document` | [✓](dotnet/console/Console/Scenarios/CreateDocumentWithOneSignerScenario.cs) | [✓](java/console/src/main/java/com/lacunasoftware/signer/sample/scenarios/CreateDocumentWithOneSignerScenario.java) | [✓](nodejs/scenarios/createDocumentWithOneSignerScenario.ts) | [✓](php/Scenarios/CreateDocumentWithOneSignerScenario.php) |
| Create documents | Create a document with an attachment<br>`create-document-with-attachment` | [✓](dotnet/console/Console/Scenarios/CreateDocumentWithAttachmentScenario.cs) | [✓](java/console/src/main/java/com/lacunasoftware/signer/sample/scenarios/CreateDocumentWithAttachmentScenario.java) | [✓](nodejs/scenarios/createDocumentWithAttachmentScenario.ts) | [✓](php/Scenarios/CreateDocumentWithAttachmentScenario.php) |
| Create documents | Create a document with a description<br>`create-document-with-description` | [✓](dotnet/console/Console/Scenarios/CreateDocumentWithDescriptionScenario.cs) | [✓](java/console/src/main/java/com/lacunasoftware/signer/sample/scenarios/CreateDocumentWithDescriptionScenario.java) | [✓](nodejs/scenarios/createDocumentWithDescriptionScenario.ts) | [✓](php/Scenarios/CreateDocumentWithDescriptionScenario.php) |
| Create documents | Merge several PDFs into one document (envelope)<br>`create-envelope` | [✓](dotnet/console/Console/Scenarios/CreateDocumentWithEnvelopeScenario.cs) | [✓](java/console/src/main/java/com/lacunasoftware/signer/sample/scenarios/CreateDocumentWithEnvelopeScenario.java) | [✓](nodejs/scenarios/createDocumentWithEnvelopeScenario.ts) | [✓](php/Scenarios/CreateDocumentWithEnvelopeScenario.php) |
| Create documents | Upload a file as Base64<br>`upload-file-base64` | — | — | — | [✓](php/Scenarios/CreateBase64DocumentScenario.php) |
| Create documents | Upload a file as multipart/form-data<br>`upload-file-multipart` | — | — | [✓](nodejs/scenarios/uploadUsingMultipartFormDataScenario.ts) | — |
| Create documents | Create a document from a large file<br>`create-document-large-file` | — | — | — | [✓](php/Scenarios/CreateLargeDocumentScenario.php) |
| Create documents | Generate a document from a PDF form template and CSV data<br>`generate-document-from-template` | — | [✓](java/console/src/main/java/com/lacunasoftware/signer/sample/scenarios/GenerateDocumentWithTwoOrMoreSignersScenario.java) | — | [✓](php/Scenarios/GenerateDocumentWithTwoOrMoreSignersScenario.php) |
| Flows: participants, order and rules | Create a document with two or more signers, in any order<br>`create-document-multiple-signers` | [✓](dotnet/console/Console/Scenarios/CreateDocumentWithTwoOrMoreSignersWithoutOrderScenario.cs) | [✓](java/console/src/main/java/com/lacunasoftware/signer/sample/scenarios/CreateDocumentWithTwoOrMoreSignersWithoutOrderScenario.java) | [✓](nodejs/scenarios/createDocumentWithTwoOrMoreSignersWithoutOrderScenario.ts) | [✓](php/Scenarios/CreateDocumentWithTwoOrMoreSignersWithoutOrderScenario.php) |
| Flows: participants, order and rules | Create a document with signers in a fixed order<br>`create-document-ordered-flow` | [✓](dotnet/console/Console/Scenarios/CreateDocumentWithTwoOrMoreSignersWithOrderScenario.cs) | [✓](java/console/src/main/java/com/lacunasoftware/signer/sample/scenarios/CreateDocumentWithTwoOrMoreSignersWithOrderScenario.java) | [✓](nodejs/scenarios/createDocumentWithTwoOrMoreSignersWithOrderScenario.ts) | [✓](php/Scenarios/CreateDocumentWithTwoOrMoreSignersWithOrderScenario.php) |
| Flows: participants, order and rules | Create a document with an approver<br>`create-document-with-approver` | [✓](dotnet/console/Console/Scenarios/CreateDocumentWithApproversScenario.cs) | [✓](java/console/src/main/java/com/lacunasoftware/signer/sample/scenarios/CreateDocumentWithApproversScenario.java) | [✓](nodejs/scenarios/createDocumentWithApproversScenario.ts) | [✓](php/Scenarios/CreateDocumentWithApproversScenario.php) |
| Flows: participants, order and rules | Create a document with a signing rule (any N of a group)<br>`create-document-with-signing-rule` | [✓](dotnet/console/Console/Scenarios/CreateDocumentWithSigningRuleScenario.cs) | [✓](java/console/src/main/java/com/lacunasoftware/signer/sample/scenarios/CreateDocumentWithSigningRuleScenario.java) | [✓](nodejs/scenarios/createDocumentWithSigningRuleScenario.ts) | [✓](php/Scenarios/CreateDocumentWithSigningRuleScenario.php) |
| Flows: participants, order and rules | Pre-position a signature on the document<br>`create-document-with-positioned-signatures` | [✓](dotnet/console/Console/Scenarios/CreateDocumentWithPositionedSignaturesScenario.cs) | [✓](java/console/src/main/java/com/lacunasoftware/signer/sample/scenarios/CreateDocumentWithPositionedSignaturesScenario.java) | [✓](nodejs/scenarios/createDocumentWithPositionedSignaturesScenario.ts) | [✓](php/Scenarios/CreateDocumentWithPositionedSignaturesScenario.php) |
| Flows: participants, order and rules | Add a signer to an existing document<br>`add-signer-to-existing-document` | — | — | — | [✓](php/Scenarios/AddSignerToExistingDocumentScenario.php) |
| Flows: participants, order and rules | Change a participant in an ongoing flow<br>`edit-flow-action` | — | — | — | [✓](php/Scenarios/DocumentFlowEditRequestScenario.php) |
| Signature formats (CAdES, XAdES) | Require a CAdES signature on a PDF<br>`create-pdf-with-cades-signature` | [✓](dotnet/console/Console/Scenarios/CreatePDFDocumentWithCadesSignatureScenario.cs) | [✓](java/console/src/main/java/com/lacunasoftware/signer/sample/scenarios/CreatePDFDocumentWithCadesSignatureScenario.java) | [✓](nodejs/scenarios/createPDFDocumentWithCadesSignatureScenario.ts) | [✓](php/Scenarios/CreatePDFDocumentWithCadesSignatureScenario.php) |
| Signature formats (CAdES, XAdES) | Sign a whole XML file (XAdES)<br>`create-xml-full-signature` | [✓](dotnet/console/Console/Scenarios/CreateXMLWithFullDocumentSignatureScenario.cs) | [✓](java/console/src/main/java/com/lacunasoftware/signer/sample/scenarios/CreateXmlWithFullDocumentSignatureScenario.java) | [✓](nodejs/scenarios/createXmlWithFullDocumentSignatureScenario.ts) | [✓](php/Scenarios/CreateXmlWithFullDocumentSignatureScenario.php) |
| Signature formats (CAdES, XAdES) | Sign one element of an XML file (XAdES)<br>`create-xml-element-signature` | [✓](dotnet/console/Console/Scenarios/CreateXMLWithElementSignatureScenario.cs) | [✓](java/console/src/main/java/com/lacunasoftware/signer/sample/scenarios/CreateXMLWithElementSignatureScenario.java) | [✓](nodejs/scenarios/createXMLWithElementSignatureScenario.ts) | [✓](php/Scenarios/CreateXMLWithElementSignatureScenario.php) |
| Signature formats (CAdES, XAdES) | Sign a Brazilian digital diploma (XAdES, Portaria MEC 554/2019)<br>`create-digital-degree` | [✓](dotnet/console/Console/Scenarios/CreateDigitalDegreeScenario.cs) | [✓](java/console/src/main/java/com/lacunasoftware/signer/sample/scenarios/CreateDigitalDegreeScenario.java) | [✓](nodejs/scenarios/createDigitalDegreeScenario.ts) | [✓](php/Scenarios/CreateDigitalDegreeScenario.php) |
| Sign through the API | Sign with a digital certificate through the API (public signature)<br>`public-signature-with-certificate` | [✓](dotnet/console/Console/Scenarios/PublicSignatureScenario.cs) | [✓](java/console/src/main/java/com/lacunasoftware/signer/sample/scenarios/PublicSignatureScenario.java) | [✓](nodejs/scenarios/publicSignatureScenario.ts) | — |
| Sign through the API | Sign electronically with an uploaded signature image<br>`electronic-signature-with-signature-image` | — | [✓](java/console/src/main/java/com/lacunasoftware/signer/sample/scenarios/ElectronicSignatureWithSignatureImageScenario.java) | — | — |
| Embedded signing | Sign without leaving your page (embedded signature)<br>`embedded-signature` | [✓](dotnet/console/Console/Scenarios/EmbeddedSignatureScenario.cs) | [✓](java/console/src/main/java/com/lacunasoftware/signer/sample/scenarios/EmbeddedSignatureScenario.java) | [✓](nodejs/scenarios/embeddedSignatureScenario.ts) | [✓](php/Scenarios/EmbeddedSignatureScenario.php) |
| Track and list documents | Check a document's status and its participants' actions<br>`check-document-status` | [✓](dotnet/console/Console/Scenarios/CheckDocumentStatusScenario.cs) | [✓](java/console/src/main/java/com/lacunasoftware/signer/sample/scenarios/CheckDocumentStatusScenario.java) | — | [✓](php/Scenarios/CheckDocumentStatusScenario.php) |
| Track and list documents | Send reminders to participants with pending actions<br>`send-signing-reminders` | [✓](dotnet/console/Console/Scenarios/NotifyFlowParticipantsScenario.cs) | [✓](java/console/src/main/java/com/lacunasoftware/signer/sample/scenarios/NotifyFlowParticipantsScenario.java) | [✓](nodejs/scenarios/notifyFlowParticipantsScenario.ts) | [✓](php/Scenarios/NotifyFlowParticipantsScenario.php) |
| Track and list documents | List documents<br>`list-documents` | [✓](dotnet/console/Console/Scenarios/ListDocumentsScenario.cs) | [✓](java/console/src/main/java/com/lacunasoftware/signer/sample/scenarios/ListDocumentsScenario.java) | — | [✓](php/Scenarios/ListDocumentsScenario.php) |
| Track and list documents | List saved document flows<br>`list-document-flows` | — | — | [✓](nodejs/scenarios/listDocumentScenario.ts) | — |
| Webhooks | Receive webhooks with a minimal HTTP endpoint<br>`webhook-endpoint` | [✓](dotnet/console/Console/Scenarios/WebhookEndpointScenario.cs) | [✓](java/console/src/main/java/com/lacunasoftware/signer/sample/scenarios/WebhookEndpointScenario.java) | [✓](nodejs/scenarios/webhookEndpointScenario.ts) | [✓](php/Scenarios/WebhookEndpointScenario.php) |
| Webhooks | Handle document webhooks (signed, approved, refused, concluded)<br>`webhook-document-events` | [✓](dotnet/console/Console/Scenarios/CheckDocumentStatusScenario.cs) | [✓](java/console/src/main/java/com/lacunasoftware/signer/sample/scenarios/CheckDocumentStatusScenario.java) | [✓](nodejs/scenarios/checkDocumentStatusScenario.ts) | [✓](php/Scenarios/CheckDocumentStatusScenario.php) |
| Webhooks | Handle the invoice-closed billing webhook<br>`webhook-invoice-closed` | [✓](dotnet/console/Console/Scenarios/InvoiceWebhookHandlingScenario.cs) | [✓](java/console/src/main/java/com/lacunasoftware/signer/sample/scenarios/InvoiceWebhookHandlingScenario.java) | — | [✓](php/Scenarios/InvoiceWebhookHandlingScenario.php) |
| Download documents | Download a document's versions<br>`download-document-versions` | [✓](dotnet/console/Console/Scenarios/DownloadDocumentVersionScenario.cs) | [✓](java/console/src/main/java/com/lacunasoftware/signer/sample/scenarios/DownloadDocumentVersionScenario.java) | [✓](nodejs/scenarios/downloadDocumentVersionScenario.ts) | [✓](php/Scenarios/DownloadDocumentVersionScenario.php) |
| Document lifecycle | Cancel a document<br>`cancel-document` | [✓](dotnet/console/Console/Scenarios/CancelDocumentScenario.cs) | [✓](java/console/src/main/java/com/lacunasoftware/signer/sample/scenarios/CancelDocumentScenario.java) | [✓](nodejs/scenarios/cancelDocumentScenario.ts) | [✓](php/Scenarios/CancelDocumentScenario.php) |
| Document lifecycle | Refuse a document as the organization<br>`refuse-document` | [✓](dotnet/console/Console/Scenarios/RefuseDocumentScenario.cs) | [✓](java/console/src/main/java/com/lacunasoftware/signer/sample/scenarios/RefuseDocumentScenario.java) | [✓](nodejs/scenarios/refuseDocumentScenario.ts) | [✓](php/Scenarios/RefuseDocumentScenario.php) |
| Document lifecycle | Delete a document<br>`delete-document` | [✓](dotnet/console/Console/Scenarios/DeleteDocumentScenario.cs) | [✓](java/console/src/main/java/com/lacunasoftware/signer/sample/scenarios/DeleteDocumentScenario.java) | [✓](nodejs/scenarios/deleteDocumentScenario.ts) | [✓](php/Scenarios/DeleteDocumentScenario.php) |
| Document lifecycle | Upload a new version of a document<br>`add-document-version` | [✓](dotnet/console/Console/Scenarios/AddNewDocumentVersionScenario.cs) | [✓](java/console/src/main/java/com/lacunasoftware/signer/sample/scenarios/AddNewDocumentVersionScenario.java) | [✓](nodejs/scenarios/addNewDocumentVersionScenario.ts) | [✓](php/Scenarios/AddNewDocumentVersionScenario.php) |
| Folders | Create a document in a new folder<br>`create-document-in-new-folder` | [✓](dotnet/console/Console/Scenarios/CreateDocumentInNewFolderScenario.cs) | [✓](java/console/src/main/java/com/lacunasoftware/signer/sample/scenarios/CreateDocumentInNewFolderScenario.java) | — | [✓](php/Scenarios/CreateDocumentInNewFolderScenario.php) |
| Folders | Create a document in an existing folder<br>`create-document-in-existing-folder` | [✓](dotnet/console/Console/Scenarios/CreateDocumentInExistingFolderScenario.cs) | [✓](java/console/src/main/java/com/lacunasoftware/signer/sample/scenarios/CreateDocumentInExistingFolder.java) | [✓](nodejs/scenarios/createDocumentInExistingFolderScenario.ts) | [✓](php/Scenarios/CreateDocumentInExistingFolder.php) |
| Billing | Mark an invoice as paid<br>`mark-invoice-paid` | [✓](dotnet/console/Console/Scenarios/InvoiceWebhookHandlingScenario.cs) | [✓](java/console/src/main/java/com/lacunasoftware/signer/sample/scenarios/InvoiceWebhookHandlingScenario.java) | — | [✓](php/Scenarios/InvoiceWebhookHandlingScenario.php) |
| | **Tasks covered** | **29 of 37** | **31 of 37** | **26 of 37** | **33 of 37** |
<!-- END GENERATED GAP MATRIX -->
