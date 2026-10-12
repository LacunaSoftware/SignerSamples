# Signer samples for PHP

This folder contains a **PHP sample project** demonstrating the usage of [Signer](https://www.dropsigner.com/).

For other languages, please visit the [repository root](https://github.com/LacunaSoftware/SignerSamples).

## Console application

The project is this `php` folder: a **console application** whose entry point is
[`index.php`](index.php), with one scenario class per integration task in [`Scenarios`](Scenarios).

To run it with [Composer](https://getcomposer.org):

1. [Download the repository](https://github.com/LacunaSoftware/SignerSamples/archive/master.zip)
   or clone it.

1. In a command prompt, go to this `php` folder and run `composer install`. It needs PHP 7.2.5
   or later.

1. In `index.php`, set the scenario to run, then run `php index.php`.

[Run a scenario](#run-a-scenario) has the details.

Signer's client lib
---------------------

The samples use the `lacuna/signer-client` client lib, installed with Composer as in
[`composer.json`](composer.json):

    "require": {
        "php": ">=7.2.5",
        "lacuna/signer-client": "1.4.10"
    }

`lacuna/signer-client` 1.4.10 brings in Guzzle 7 and `jms/serializer` 3, so the project needs PHP
7.2.5 or later, Guzzle 7's minimum. The samples call the client lib only, never Guzzle directly.
`composer install` needs no extra flags or environment variables, on PHP 8 too. To check the
installed packages against published security advisories, run `composer audit`. On PHP 8.4 and
later, the client lib's models print `Deprecated` notices about implicitly nullable parameters;
they are harmless.

## Samples in this project

Each file in [`Scenarios`](Scenarios) is one Lacuna Signer integration task, calling the Signer
API through the `lacuna/signer-client` Composer package (1.4.10 in [`composer.json`](composer.json)).
The table is generated from the repository's catalog ([samples.json](../samples.json), also shown
as [SAMPLES.md](../SAMPLES.md)), where each slug has its docs link and the same task in other
languages. Before changing anything, read [AGENTS.md](../AGENTS.md).

<!-- BEGIN GENERATED SAMPLES LIST (project: php) -->
| Sample | Area | Entry file | Status | Command |
|---|---|---|---|---|
| Sign a document: create it with one signer<br>`sign-a-document` | Create documents | [CreateDocumentWithOneSignerScenario.php](Scenarios/CreateDocumentWithOneSignerScenario.php) | `current` | `composer install && php index.php` |
| Create a document with an attachment<br>`create-document-with-attachment` | Create documents | [CreateDocumentWithAttachmentScenario.php](Scenarios/CreateDocumentWithAttachmentScenario.php) | `current` | `composer install && php index.php` |
| Create a document with a description<br>`create-document-with-description` | Create documents | [CreateDocumentWithDescriptionScenario.php](Scenarios/CreateDocumentWithDescriptionScenario.php) | `current` | `composer install && php index.php` |
| Merge several PDFs into one document (envelope)<br>`create-envelope` | Create documents | [CreateDocumentWithEnvelopeScenario.php](Scenarios/CreateDocumentWithEnvelopeScenario.php) | `current` | `composer install && php index.php` |
| Upload a file as Base64<br>`upload-file-base64` | Create documents | [CreateBase64DocumentScenario.php](Scenarios/CreateBase64DocumentScenario.php) | `current` | `composer install && php index.php` |
| Create a document from a large file<br>`create-document-large-file` | Create documents | [CreateLargeDocumentScenario.php](Scenarios/CreateLargeDocumentScenario.php) | `current` | `composer install && php index.php` |
| Generate a document from a PDF form template and CSV data<br>`generate-document-from-template` | Create documents | [GenerateDocumentWithTwoOrMoreSignersScenario.php](Scenarios/GenerateDocumentWithTwoOrMoreSignersScenario.php) | `current` | `composer install && php index.php` |
| Create a document with two or more signers, in any order<br>`create-document-multiple-signers` | Flows: participants, order and rules | [CreateDocumentWithTwoOrMoreSignersWithoutOrderScenario.php](Scenarios/CreateDocumentWithTwoOrMoreSignersWithoutOrderScenario.php) | `current` | `composer install && php index.php` |
| Create a document with signers in a fixed order<br>`create-document-ordered-flow` | Flows: participants, order and rules | [CreateDocumentWithTwoOrMoreSignersWithOrderScenario.php](Scenarios/CreateDocumentWithTwoOrMoreSignersWithOrderScenario.php) | `current` | `composer install && php index.php` |
| Create a document with an approver<br>`create-document-with-approver` | Flows: participants, order and rules | [CreateDocumentWithApproversScenario.php](Scenarios/CreateDocumentWithApproversScenario.php) | `current` | `composer install && php index.php` |
| Create a document with a signing rule (any N of a group)<br>`create-document-with-signing-rule` | Flows: participants, order and rules | [CreateDocumentWithSigningRuleScenario.php](Scenarios/CreateDocumentWithSigningRuleScenario.php) | `current` | `composer install && php index.php` |
| Pre-position a signature on the document<br>`create-document-with-positioned-signatures` | Flows: participants, order and rules | [CreateDocumentWithPositionedSignaturesScenario.php](Scenarios/CreateDocumentWithPositionedSignaturesScenario.php) | `current` | `composer install && php index.php` |
| Add a signer to an existing document<br>`add-signer-to-existing-document` | Flows: participants, order and rules | [AddSignerToExistingDocumentScenario.php](Scenarios/AddSignerToExistingDocumentScenario.php) | `current` | `composer install && php index.php` |
| Change a participant in an ongoing flow<br>`edit-flow-action` | Flows: participants, order and rules | [DocumentFlowEditRequestScenario.php](Scenarios/DocumentFlowEditRequestScenario.php) | `current` | `composer install && php index.php` |
| Require a CAdES signature on a PDF<br>`create-pdf-with-cades-signature` | Signature formats (CAdES, XAdES) | [CreatePDFDocumentWithCadesSignatureScenario.php](Scenarios/CreatePDFDocumentWithCadesSignatureScenario.php) | `current` | `composer install && php index.php` |
| Sign a whole XML file (XAdES)<br>`create-xml-full-signature` | Signature formats (CAdES, XAdES) | [CreateXmlWithFullDocumentSignatureScenario.php](Scenarios/CreateXmlWithFullDocumentSignatureScenario.php) | `current` | `composer install && php index.php` |
| Sign one element of an XML file (XAdES)<br>`create-xml-element-signature` | Signature formats (CAdES, XAdES) | [CreateXMLWithElementSignatureScenario.php](Scenarios/CreateXMLWithElementSignatureScenario.php) | `current` | `composer install && php index.php` |
| Sign a Brazilian digital diploma (XAdES, Portaria MEC 554/2019)<br>`create-digital-degree` | Signature formats (CAdES, XAdES) | [CreateDigitalDegreeScenario.php](Scenarios/CreateDigitalDegreeScenario.php) | `current` | `composer install && php index.php` |
| Sign without leaving your page (embedded signature)<br>`embedded-signature` | Embedded signing | [EmbeddedSignatureScenario.php](Scenarios/EmbeddedSignatureScenario.php) | `current` | `composer install && php index.php` |
| Check a document's status and its participants' actions<br>`check-document-status` | Track and list documents | [CheckDocumentStatusScenario.php](Scenarios/CheckDocumentStatusScenario.php) | `current` | `composer install && php index.php` |
| Send reminders to participants with pending actions<br>`send-signing-reminders` | Track and list documents | [NotifyFlowParticipantsScenario.php](Scenarios/NotifyFlowParticipantsScenario.php) | `current` | `composer install && php index.php` |
| List documents<br>`list-documents` | Track and list documents | [ListDocumentsScenario.php](Scenarios/ListDocumentsScenario.php) | `legacy` | `composer install && php index.php` |
| Receive webhooks with a minimal HTTP endpoint<br>`webhook-endpoint` | Webhooks | [WebhookEndpointScenario.php](Scenarios/WebhookEndpointScenario.php) | `current` | `composer install && php -S localhost:8080 webhook.php` |
| Handle document webhooks (signed, approved, refused, concluded)<br>`webhook-document-events` | Webhooks | [CheckDocumentStatusScenario.php](Scenarios/CheckDocumentStatusScenario.php) | `current` | `composer install && php index.php` |
| Handle the invoice-closed billing webhook<br>`webhook-invoice-closed` | Webhooks | [InvoiceWebhookHandlingScenario.php](Scenarios/InvoiceWebhookHandlingScenario.php) | `current` | `composer install && php index.php` |
| Mark an invoice as paid<br>`mark-invoice-paid` | Billing | [InvoiceWebhookHandlingScenario.php](Scenarios/InvoiceWebhookHandlingScenario.php) | `current` | `composer install && php index.php` |
| Download a document's versions<br>`download-document-versions` | Download documents | [DownloadDocumentVersionScenario.php](Scenarios/DownloadDocumentVersionScenario.php) | `current` | `composer install && php index.php` |
| Cancel a document<br>`cancel-document` | Document lifecycle | [CancelDocumentScenario.php](Scenarios/CancelDocumentScenario.php) | `current` | `composer install && php index.php` |
| Refuse a document as the organization<br>`refuse-document` | Document lifecycle | [RefuseDocumentScenario.php](Scenarios/RefuseDocumentScenario.php) | `current` | `composer install && php index.php` |
| Delete a document<br>`delete-document` | Document lifecycle | [DeleteDocumentScenario.php](Scenarios/DeleteDocumentScenario.php) | `current` | `composer install && php index.php` |
| Upload a new version of a document<br>`add-document-version` | Document lifecycle | [AddNewDocumentVersionScenario.php](Scenarios/AddNewDocumentVersionScenario.php) | `current` | `composer install && php index.php` |
| Create a document in a new folder<br>`create-document-in-new-folder` | Folders | [CreateDocumentInNewFolderScenario.php](Scenarios/CreateDocumentInNewFolderScenario.php) | `current` | `composer install && php index.php` |
| Create a document in an existing folder<br>`create-document-in-existing-folder` | Folders | [CreateDocumentInExistingFolder.php](Scenarios/CreateDocumentInExistingFolder.php) | `current` | `composer install && php index.php` |
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

`init()` of [`Scenarios/Scenario.php`](Scenarios/Scenario.php) reads the Signer instance URL
from the `SIGNER_ENDPOINT` environment variable and the API key from `SIGNER_API_KEY`
(`$this->endPoint` and `$this->apiKey`). When a variable is unset or empty, it falls back to the
Signer sandbox, `https://signer-lac.azurewebsites.net/`, and a shared sample key, so the samples
run as they are. The sandbox is where you should develop and test: to use your own organization
there, set `SIGNER_API_KEY` to one of your keys. For production, set both variables together.
[`.env.example`](.env.example) lists every value the project uses, including the webhook
endpoint's; nothing loads that file automatically.

Set the variables in the shell that runs PHP; `php -S` passes them on to the webhook endpoint too.
In bash, quoting the key because of its `|`:

```bash
export SIGNER_ENDPOINT=https://signer-lac.azurewebsites.net
export SIGNER_API_KEY='your-application|your-key'
php index.php
```

In PowerShell:

```powershell
$env:SIGNER_ENDPOINT = "https://signer-lac.azurewebsites.net"
$env:SIGNER_API_KEY = "your-application|your-key"
php index.php
```

Prerequisites of some scenarios:

- `CreateDocumentInExistingFolder`: the organization needs a folder named "Sample Folder".
- `CreateLargeDocumentScenario`: add a large PDF (the comment suggests over 10 MB) as
  `resources/bigsample.pdf`.
- `InvoiceWebhookHandlingScenario::run`: needs an API key with billing administration privileges
  and a real invoice id.

## Receive webhooks locally

[`WebhookEndpointScenario`](Scenarios/WebhookEndpointScenario.php) is a minimal webhook endpoint
for PHP's built-in web server, with [`webhook.php`](webhook.php) as the router script. Served with
`php -S localhost:8080 webhook.php`, it answers on `http://localhost:8080/webhook` (change the
port in that command) and follows the
[webhooks docs](https://docs.lacunasoftware.com/en-us/articles/signer/webhooks#best-practices):

1. It answers 401 unless the `Authorization` header is `Bearer <token>` with the token you
   registered, set in `SIGNER_WEBHOOK_TOKEN` (default `my-webhook-token`), because Signer does not
   sign the body.
2. It answers 200 at once (`fastcgi_finish_request()` under PHP-FPM, `flush()` elsewhere) and
   processes the event afterwards. Signer retries a non-2xx answer or a connection error, up to 10
   times by default.
3. It ignores an event it has already received, keyed on `type`, the document `id` and, when there
   is one, `flowActionId`. PHP keeps nothing in memory between requests, so it records each key as
   an empty file in `signer-webhook-events` under the temp folder (delete the folder to forget
   them); production needs durable storage, such as a table with a unique index on the key.
4. Events can arrive out of order, so before acting on `DocumentConcluded` it reads the document
   back (`GET /api/documents/{id}`) and checks that its `status` is `Concluded`. This is its only
   call to Signer.

It then passes the body to `CheckDocumentStatusScenario::HandleWebhook`, which switches on `type`,
and writes what it echoes to the server's console. PHP's built-in server handles one request at a
time, which is enough for local tests; in production, run the endpoint under PHP-FPM.

To test it without Signer, run `composer install && php -S localhost:8080 webhook.php` from this
folder, and post a `DocumentSigned` event with the default token from bash (Git Bash on Windows):

```bash
curl -i http://localhost:8080/webhook \
  -H "Authorization: Bearer my-webhook-token" \
  -H "Content-Type: application/json" \
  -d '{"type":"DocumentSigned","data":{"id":"3fa85f64-5717-4562-b3fc-2c963f66afa6","name":"sample.pdf",
       "signature":{"flowActionId":"5c1a2b3c-4d5e-4f60-8a7b-9c0d1e2f3a4b","name":"Jack Bauer",
       "identifier":"75502846369","emailAddress":"jack.bauer@mailinator.com"}}}'
```

The answer is `200`, and the server's console prints `Accepted: ...` and `Document ... is Signed`.
Post it again and the console prints `Ignored a repeated event`; change the token and the answer is
`401`. A `DocumentConcluded` body, `{"type":"DocumentConcluded","data":{"id":"<document id>"}}`,
makes the endpoint read that document from Signer after answering; with a made-up id, that call
fails. On PHP 8.4 and later, the client library's models also print `Deprecated` notices there;
they are harmless.

To receive real events:

1. Expose port 8080 with an HTTPS tunnel (ngrok, Cloudflare Tunnel, ...), for example
   `ngrok http 8080`, because Signer calls the endpoint from its own servers.
2. In Signer, under **Organization → Integration**, register `https://<tunnel host>/webhook` with
   the auth type `Bearer` and a long random token, or call
   `POST /api/organizations/{orgId}/webhooks` with `url`, `authType` and `token`. Set
   `SIGNER_WEBHOOK_TOKEN` to the same token (see Configuration) before you start `php -S`.
3. Webhooks belong to an organization (personal organizations have none), so register it in your
   own organization and create the documents there, with its API key (see Configuration).

`InvoiceClosed` is sent by the instance's billing webhook, which the system administrator
configures, not by an organization webhook. `InvoiceWebhookHandlingScenario::HandleWebhook`
handles it, but no endpoint calls it.
