This folder contains **PHP sample projects** demonstrating the usage of [Signer](https://www.dropsigner.com/).

For other languages, please visit the [repository root](https://github.com/LacunaSoftware/SignerSamples).

## Default sample

A sample **console application** can be fond in the in the index.php file

You can user the following tool to run the application:

**Using [Composer](http://getcomposer.org)**

1. [Download the project](https://github.com/LacunaSoftware/SignerSamples/archive/master.zip)
   or clone the repository

1. In a command prompt, navigate to the folder `console` and run the command
   `composer install`.
   

Opening the samples
----------------------

The samples are located in the folder Scenarios, and you can test each one by calling them in the index.php file.

Running samples
----------------------

Use the command prompt and run the command bellow to execute the example:

php index.php


Signer's client lib
---------------------

The recommended way to install **Signer client lib** is through [Composer](http://getcomposer.org):

    "require": {
        "lacuna/signer-client" : "1.4.5"
    }

This library depends on the GuzzleHttp package, which in turn requires PHP 5.5 or greater.


## Samples in this project

Each file in [`Scenarios`](Scenarios) is one Lacuna Signer integration task, calling the Signer
API through the `lacuna/signer-client` Composer package (1.4.8 in [`composer.json`](composer.json)).
The table is generated from the repository's catalog ([samples.json](../samples.json), also shown
as [SAMPLES.md](../SAMPLES.md)), where each slug has its docs link and the same task in other
languages. Before changing anything, read [AGENTS.md](../AGENTS.md).

<!-- BEGIN GENERATED SAMPLES LIST (project: php) -->
| Sample | Area | Entry file | Status |
|---|---|---|---|
| Sign a document: create it with one signer<br>`sign-a-document` | Create documents | [CreateDocumentWithOneSignerScenario.php](Scenarios/CreateDocumentWithOneSignerScenario.php) | `current` |
| Create a document with an attachment<br>`create-document-with-attachment` | Create documents | [CreateDocumentWithAttachmentScenario.php](Scenarios/CreateDocumentWithAttachmentScenario.php) | `current` |
| Create a document with a description<br>`create-document-with-description` | Create documents | [CreateDocumentWithDescriptionScenario.php](Scenarios/CreateDocumentWithDescriptionScenario.php) | `current` |
| Merge several PDFs into one document (envelope)<br>`create-envelope` | Create documents | [CreateDocumentWithEnvelopeScenario.php](Scenarios/CreateDocumentWithEnvelopeScenario.php) | `current` |
| Upload a file as Base64<br>`upload-file-base64` | Create documents | [CreateBase64DocumentScenario.php](Scenarios/CreateBase64DocumentScenario.php) | `current` |
| Create a document from a large file<br>`create-document-large-file` | Create documents | [CreateLargeDocumentScenario.php](Scenarios/CreateLargeDocumentScenario.php) | `current` |
| Generate a document from a PDF form template and CSV data<br>`generate-document-from-template` | Create documents | [GenerateDocumentWithTwoOrMoreSignersScenario.php](Scenarios/GenerateDocumentWithTwoOrMoreSignersScenario.php) | `current` |
| Create a document with two or more signers, in any order<br>`create-document-multiple-signers` | Flows: participants, order and rules | [CreateDocumentWithTwoOrMoreSignersWithoutOrderScenario.php](Scenarios/CreateDocumentWithTwoOrMoreSignersWithoutOrderScenario.php) | `current` |
| Create a document with signers in a fixed order<br>`create-document-ordered-flow` | Flows: participants, order and rules | [CreateDocumentWithTwoOrMoreSignersWithOrderScenario.php](Scenarios/CreateDocumentWithTwoOrMoreSignersWithOrderScenario.php) | `current` |
| Create a document with an approver<br>`create-document-with-approver` | Flows: participants, order and rules | [CreateDocumentWithApproversScenario.php](Scenarios/CreateDocumentWithApproversScenario.php) | `current` |
| Create a document with a signing rule (any N of a group)<br>`create-document-with-signing-rule` | Flows: participants, order and rules | [CreateDocumentWithSigningRuleScenario.php](Scenarios/CreateDocumentWithSigningRuleScenario.php) | `current` |
| Pre-position a signature on the document<br>`create-document-with-positioned-signatures` | Flows: participants, order and rules | [CreateDocumentWithPositionedSignaturesScenario.php](Scenarios/CreateDocumentWithPositionedSignaturesScenario.php) | `current` |
| Add a signer to an existing document<br>`add-signer-to-existing-document` | Flows: participants, order and rules | [AddSignerToExistingDocumentScenario.php](Scenarios/AddSignerToExistingDocumentScenario.php) | `current` |
| Change a participant in an ongoing flow<br>`edit-flow-action` | Flows: participants, order and rules | [DocumentFlowEditRequestScenario.php](Scenarios/DocumentFlowEditRequestScenario.php) | `current` |
| Require a CAdES signature on a PDF<br>`create-pdf-with-cades-signature` | Signature formats (CAdES, XAdES) | [CreatePDFDocumentWithCadesSignatureScenario.php](Scenarios/CreatePDFDocumentWithCadesSignatureScenario.php) | `current` |
| Sign a whole XML file (XAdES)<br>`create-xml-full-signature` | Signature formats (CAdES, XAdES) | [CreateXmlWithFullDocumentSignatureScenario.php](Scenarios/CreateXmlWithFullDocumentSignatureScenario.php) | `current` |
| Sign one element of an XML file (XAdES)<br>`create-xml-element-signature` | Signature formats (CAdES, XAdES) | [CreateXMLWithElementSignatureScenario.php](Scenarios/CreateXMLWithElementSignatureScenario.php) | `current` |
| Sign a Brazilian digital diploma (XAdES, Portaria MEC 554/2019)<br>`create-digital-degree` | Signature formats (CAdES, XAdES) | [CreateDigitalDegreeScenario.php](Scenarios/CreateDigitalDegreeScenario.php) | `current` |
| Sign without leaving your page (embedded signature)<br>`embedded-signature` | Embedded signing | [EmbeddedSignatureScenario.php](Scenarios/EmbeddedSignatureScenario.php) | `current` |
| Check a document's status and its participants' actions<br>`check-document-status` | Track and list documents | [CheckDocumentStatusScenario.php](Scenarios/CheckDocumentStatusScenario.php) | `current` |
| Send reminders to participants with pending actions<br>`send-signing-reminders` | Track and list documents | [NotifyFlowParticipantsScenario.php](Scenarios/NotifyFlowParticipantsScenario.php) | `current` |
| List documents<br>`list-documents` | Track and list documents | [ListDocumentsScenario.php](Scenarios/ListDocumentsScenario.php) | `legacy` |
| Handle document webhooks (signed, approved, refused, concluded)<br>`webhook-document-events` | Webhooks | [CheckDocumentStatusScenario.php](Scenarios/CheckDocumentStatusScenario.php) | `current` |
| Handle the invoice-closed billing webhook<br>`webhook-invoice-closed` | Webhooks | [InvoiceWebhookHandlingScenario.php](Scenarios/InvoiceWebhookHandlingScenario.php) | `current` |
| Mark an invoice as paid<br>`mark-invoice-paid` | Billing | [InvoiceWebhookHandlingScenario.php](Scenarios/InvoiceWebhookHandlingScenario.php) | `current` |
| Download a document's versions<br>`download-document-versions` | Download documents | [DownloadDocumentVersionScenario.php](Scenarios/DownloadDocumentVersionScenario.php) | `current` |
| Cancel a document<br>`cancel-document` | Document lifecycle | [CancelDocumentScenario.php](Scenarios/CancelDocumentScenario.php) | `current` |
| Refuse a document as the organization<br>`refuse-document` | Document lifecycle | [RefuseDocumentScenario.php](Scenarios/RefuseDocumentScenario.php) | `current` |
| Delete a document<br>`delete-document` | Document lifecycle | [DeleteDocumentScenario.php](Scenarios/DeleteDocumentScenario.php) | `current` |
| Upload a new version of a document<br>`add-document-version` | Document lifecycle | [AddNewDocumentVersionScenario.php](Scenarios/AddNewDocumentVersionScenario.php) | `current` |
| Create a document in a new folder<br>`create-document-in-new-folder` | Folders | [CreateDocumentInNewFolderScenario.php](Scenarios/CreateDocumentInNewFolderScenario.php) | `current` |
| Create a document in an existing folder<br>`create-document-in-existing-folder` | Folders | [CreateDocumentInExistingFolder.php](Scenarios/CreateDocumentInExistingFolder.php) | `current` |
<!-- END GENERATED SAMPLES LIST -->

## Run a scenario

The project is this `php` folder; there is no `console` subfolder. From here:

1. In [`index.php`](index.php), set the scenario to run:
   `$signerSample = new CreateDocumentWithOneSignerScenario();`
2. Run:

   ```bash
   composer install && php index.php
   ```

Scenarios open `resources/...` relative to the working directory, so run them from this folder.
Every run creates real documents on the configured Signer instance, and Signer e-mails the sample
participants (made-up `@mailinator.com` addresses).

## Configuration

The Signer instance URL and API key are hard-coded in `init()` of
[`Scenarios/Scenario.php`](Scenarios/Scenario.php) (`$this->endPoint` and `$this->apiKey`). They
point to the Signer sandbox, `https://signer-lac.azurewebsites.net/`, with a shared sample key.
The sandbox is where you should develop and test: to use your own organization there, replace the
key with one of yours. For production, change the URL and the key together.
[`.env.example`](.env.example) lists every value the project uses; it is documentation only,
because nothing loads it.

Prerequisites of some scenarios:

- `CreateDocumentInExistingFolder`: the organization needs a folder named "Sample Folder".
- `CreateLargeDocumentScenario`: add a large PDF (the comment suggests over 10 MB) as
  `resources/bigsample.pdf`.
- `InvoiceWebhookHandlingScenario::run`: needs an API key with billing administration privileges
  and a real invoice id.

## Receive webhooks locally

`CheckDocumentStatusScenario::HandleWebhook` (document events) and
`InvoiceWebhookHandlingScenario::HandleWebhook` (`InvoiceClosed`) take the raw JSON body, but
nothing calls them. To try one:

1. Write a small endpoint of your own outside this repository's samples, for example a
   `webhook.php` that requires `vendor/autoload.php`, answers 200, and passes
   `file_get_contents('php://input')` to the handler. Serve it locally with
   `php -S localhost:8000 webhook.php`.
2. Expose it with an HTTPS tunnel (ngrok, Cloudflare Tunnel, ...), because Signer calls it from its
   own servers.
3. Register the public URL in Signer under **Organization → Integration**, with an auth type
   other than `NoAuth`, and check that header in your endpoint.

To test without Signer, post a body copied from the
[webhooks docs](https://docs.lacunasoftware.com/en-us/articles/signer/webhooks) to your local
endpoint. `InvoiceClosed` is sent by the instance's billing webhook, which the system
administrator configures, not by an organization webhook.
