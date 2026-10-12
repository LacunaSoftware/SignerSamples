# AGENTS.md

Guidance for AI coding agents (Claude Code, Cursor, Copilot, Codex, ...) working in this
repository, usually for a developer who is integrating **Lacuna Signer** (sold as
[Dropsigner](https://www.dropsigner.com/)) into their own application.

## 1. What this repository is (and is not)

It **is** four console projects, one per language (.NET, Java, Node.js, PHP). Each holds a few
dozen *scenarios*: one file per integration task, calling the Signer REST API through Lacuna's
client library for that language. A scenario creates documents, builds flows, gets signing
URLs, downloads files, or shows how to handle a webhook.

It is **not** a production template. Copy the API calls, not the scaffolding:

- The Signer URL and API key are hard-coded in each project's base `Scenario` class (section 3).
  No sample reads a config file or an environment variable.
- The committed key, `API Sample App|…`, belongs to Lacuna's **sandbox**,
  `https://signer-lac.azurewebsites.net`, the instance clients should develop and test against.
  Do not edit, move, rotate or "clean up" the key. Your client needs their own key, created in
  their own organization (**Applications → Keys**); it is shown only once.
- **Running a scenario creates real documents** on that instance, and Signer e-mails the
  participants. The samples use made-up people at `@mailinator.com` (some files misspell it
  `@mailnator.com`; fix that first) with test CPFs such as `75502846369`. Never put a real
  person's data in a sample run.
- There is no web server. Webhook handlers are plain `HandleWebhook` methods that nothing calls,
  and the embedded-signature scenario only prints the URL you would embed.
- `nodejs/samples/56072386105.pfx` (password `1234`) is a Lacuna **test** certificate (issuer
  *Lacuna CA Test v7*). In a real integration the key stays on the signer's device.
- Some samples have known defects, marked **"Caveat:"** in the catalog's `description`.
- docs.lacunasoftware.com links to these files by path. Do not rename or move them.

## 2. Find the right sample: use the catalog first

- **[`samples.json`](samples.json)**: one entry per (task, language), with the schema
  [`schema/samples.schema.json`](schema/samples.schema.json) shared with PkiSuiteSamples.
- **[`SAMPLES.md`](SAMPLES.md)**: the same data as tables per language. Generated; never edit it.

Each entry has: `id` (`<task>-signer`), `title`, `description`, `product` (`signer`), `area`,
`language`, `framework` (`console`), `path` (the scenario file), `related_files` (the scenario
picker, the base class with the config and shared helpers, and input files), `status`,
`docs_url`, `demo_url`, and `run` (`cwd`, `command`; `url` is null, as nothing serves a page).
Areas: `documents`, `flows`, `signature-formats`, `signing`, `embedded-signing`, `tracking`,
`webhooks`, `downloads`, `lifecycle`, `folders`, `billing`.

The same task slug in two languages means the same integration pattern, so a sample in one
language is a reliable guide for another. Read `path` first, then `related_files`; many scenarios
start with a shared `createDocument` helper in the base class (upload `sample.pdf`, one signer).

```bash
python -c "import json;[print(e['id'],e['path']) for e in json.load(open('samples.json',encoding='utf-8')) if e['language']=='java']"
```

## 3. Layout, how to run, where config lives

| Project | Stack | Run from | Command | Pick the scenario in |
|---|---|---|---|---|
| `dotnet/console` | .NET Framework 4.8, NuGet `packages.config` | `dotnet/console` | Open `Console.sln` in Visual Studio, F5 | `Console/Program.cs` (`new XScenario()`) |
| `java/console` | Java 8, Gradle 5.2.1 wrapper | `java/console` | `./gradlew run` (`gradlew.bat run` on Windows) | `src/main/java/.../sample/App.java` |
| `nodejs` | TypeScript; compiled JS committed in `scenarios/dist` | `nodejs` | `npm install && cd scenarios/dist && node <scenario>.js` | each file is its own script |
| `php` | PHP CLI, Composer | `php` | `composer install && php index.php` | `index.php` (`new XScenario()`) |

The Signer URL and API key are set in (the public-signature scenarios also hard-code a PFX):

| Project | File | Values |
|---|---|---|
| .NET | `dotnet/console/Console/Scenarios/Scenario.cs` | `domain`, `token` (constructor) |
| Java | `java/console/src/main/java/com/lacunasoftware/signer/sample/scenarios/Scenario.java` | `domain`, `token` in `Init()` |
| Node.js | `nodejs/scenarios/scenario.ts` | `apiKey`, `basePath` in the exported `config` |
| PHP | `php/Scenarios/Scenario.php` | `$this->endPoint`, `$this->apiKey` in `init()` |

Each project folder has a `.env.example` listing these values with placeholders. **No project
loads it**; it is documentation only. The samples use the sandbox,
`https://signer-lac.azurewebsites.net`, where clients should also develop and test. Production is
Dropsigner SaaS, `https://www.dropsigner.com` (the docs' examples use it), or the client's own
on-premises URL. Each instance has its own database and keys, so a client needs one key for the
sandbox and another for production.

Per-project notes:

- **.NET**: input files are copied to the output folder. `Console.csproj` does not glob, so list
  every scenario file in it (`AddNewDocumentVersionScenario.cs` is missing). Public signature
  expects `56072386105.pfx` next to the executable.
- **Java**: input files live in `src/main/resources/static/` and load through `Util`. Gradle is
  the only build (`signer-client` 2.13.0; there is no Maven build); Gradle 5.2.1 needs JDK 8-11.
- **Node.js**: files open relative to the working directory, so most scenarios run from
  `scenarios/dist`, and the six that call the shared `CreateDocument()` helper from `scenarios`
  (`node dist/<x>.js`, Windows only). Run `publicSignatureScenario.ts`, which isn't compiled, with
  `npx ts-node scenarios/publicSignatureScenario.ts` from `nodejs`. ts-node is the intended way
  for all of them once scenarios resolve files from `__dirname`, and `dist` will then be dropped;
  until then use the catalog's `run.command`, and don't regenerate `dist` with `tsc`.
- **PHP**: run from `php/`; scenarios open `resources/...` relative to it.

## 4. The Signer integration model, as the samples show it

**Authentication.** Every call sends `X-Api-Key: <application-name>|<key>`. The client libraries
take the instance URL and key in their constructor (`SignerClient(domain, token)`; Node.js
`new Configuration({ apiKey, basePath })`). A key sees and creates its own organization's
documents only.

**A document is created in two calls: upload, then create.**

1. Upload the file and get an upload id: the client's `UploadFileAsync` / `uploadFile`,
   `POST /api/uploads/bytes` with Base64 (Node.js `apiUploadsBytesPost`, PHP `uploadFileBytes`),
   or multipart `POST /api/uploads` (Node.js `apiUploadsPost`; the docs prefer it for big files).
2. `POST /api/documents` with `files` (upload `id`, `name`, `contentType`, `displayName`) and
   `flowActions`. The response is a **list** with one `{ uploadId, documentId }` per file, and
   the samples take the first item. Several files make several documents sharing one flow,
   unless `isEnvelope: true` plus `envelopeName` merges them (PDFs only) into one.

The samples also set `description`, `attachments` (with `isPrivate`), `folderId` or
`newFolderName`, `forceCadesSignature` (PDFs default to PAdES) and `xmlNamespaces`. A document
from a PDF form template and a CSV uses a separate call, `generateDocument` (Java, PHP).

**A flow is a list of flow actions.** Each has a `type`: `Signer`, `Approver`, `SignRule` or
`ApproverRule` (no sample uses the last). A rule is **one** action with `signRuleUsers` and
`numberRequiredSignatures`. `step` orders them: the same step runs in parallel, a later step is
notified only when the previous one finishes, and with no `step` at all, any order goes. Per
action the samples also set `prePositionedMarks` (page, position, size), `xadesOptions`
(`FullXml`, or `XmlElement` by `Id` or XPath) and `allowElectronicSignature`. A running flow is
changed with `POST /api/documents/{id}/flow` (`addedFlowActions`, `editedFlowActions`; PHP only).
A new version (`POST /api/documents/{id}/versions`) **resets the flow**.

**Signers.** A participant is `user: { name, email, identifier }`; the identifier is a CPF in the
samples. By default the participant gets an e-mail link and signs without logging in.
`allowElectronicSignature` allows signing without a certificate, and the action-URL request can
add `requireEmailAuthentication` (a code sent by e-mail). The API reference has more
`require…AuthenticationToSignElectronically` flags (SMS, WhatsApp, selfie, ...), but no sample
uses them. The demo site notes that Signer looks participants up by e-mail and may keep a known
user's stored name and CPF, so read the document back instead of trusting what you sent.

**Redirect or embed.** `POST /api/documents/{id}/action-url` with the participant's `identifier`
and/or `emailAddress` returns `url` (redirect the user to Signer's signing page) and `embedUrl`
(render it in your page with the LacunaSignerWidget: `new LacunaSignerWidget()`,
`widget.on(widget.events.documentSigned, …)`, `widget.render(embedUrl, elementId)`). To embed,
create the document with `disablePendingActionNotifications: true` so nobody is e-mailed. The
instance needs a Web PKI license for your domain. Whoever opens `embedUrl` signs as that
participant, so never log it.

Signing through the API, without Signer's page: `public-signature-with-certificate` takes the
document key and `ticket` from the action URL, calls `startPublicSignature` with the certificate,
signs the returned hash, then calls `completePublicSignature`.
`electronic-signature-with-signature-image` (Java) posts to
`/api/documents/keys/{key}/electronic-signature`. Both are supported for client integrations,
though not yet in the public API reference. Copy public signature from .NET or Node.js: the Java
one is a deprecated stub.

**Learning that a document is complete: use a webhook, not polling.** Polling is
`GET /api/documents/{id}` (`GetDocumentDetailsAsync` / `getDocumentDetails`). Document `status`
is one of `Pending`, `FlowConcluded` (all actions done, final processing still running),
`Concluded`, `Refused`, `Canceled` or `Expired`. Each flow action's `status` is one of `Created`
(waiting for an earlier step), `Pending`, `Completed` or `Refused`. The docs say not to poll in
production. To list documents (`GET /api/documents`), filter with `Status`
(`DocumentFilterStatus`, as the .NET and Java samples do); `IsConcluded` is deprecated. The PHP
sample still uses it: `lacuna/signer-client` (up to 1.4.10) cannot send `Status`.

A webhook is registered per organization (**Organization → Integration**, or
`POST /api/organizations/{orgId}/webhooks` with `url`, `authType`, `token`). Signer `POST`s
`{ "type": "...", "data": { ... } }`. The event types are `DocumentsCreated`, `DocumentSigned`,
`DocumentApproved`, `DocumentRefused`, `DocumentConcluded`, `DocumentCanceled`,
`DocumentExpired`, `DocumentDeleted` and `InvoiceClosed`; the last comes from the instance's
billing webhook. A webhook handler must:

1. Check the auth header you registered (Signer does not sign the body).
2. Answer 2xx quickly and work afterwards. Failures and non-2xx answers are retried, by default up
   to 10 times.
3. Be idempotent, keyed on `type` + document `id` (+ `flowActionId`), because an event can repeat.
4. Not assume ordering, and read `GET /api/documents/{id}` before any sensitive action.

The samples' `HandleWebhook` shows only the switch on `type`. In Java, read `data` with
`signerClient.getGson()` (or `getJackson()`), which handles the API's date fields.

**Getting the files.**

- Calls: `GET /api/documents/{id}/content?type=…` (a stream), `content-b64` (Base64 in JSON), and
  `GET /api/documents/{id}/ticket?type=…` (a temporary download URL).
- Client methods: `GetDocumentContentAsync` / `getDocumentContent`, `GetDocumentBytesAsync` /
  `getDocumentBytes`, `GetDocumentDownloadTicketAsync` / `getDocumentDownloadTicket`; Node.js
  `apiDocumentsIdContentGet`, `apiDocumentsIdContentB64Get`, `apiDocumentsIdTicketGet`.
- Types: `Signatures` is the signed file (a PDF with the signatures embedded, or a `.p7s` for
  CAdES). `PrinterFriendlyVersion` is a PDF with the signatures manifest. The others are
  `Original`, `OriginalWithMarks`, `SignaturesManifest`, `SignatureMarks` and `SigningTags`.
- Canceled, expired or refused documents download only as `Original` or `PrinterFriendlyVersion`.
  Only the .NET sample downloads `Signatures`; none downloads `PrinterFriendlyVersion`.

## 5. Pitfalls agents get wrong

1. **The URL and the key don't match.** A sandbox key fails on `www.dropsigner.com`, and the
   reverse fails too. Change both values together in the base class.
2. **Calling a document signed too early.** `DocumentSigned` fires once per participant, and a
   `Completed` flow action covers one participant only. The document is finished when its
   `status` is `Concluded` or `DocumentConcluded` arrives. The widget's `documentSigned` event is
   only a hint: confirm it on the server.
3. **Webhooks that aren't idempotent**, that do slow work before answering 2xx, or that trust the
   order of events (section 4).
4. **Step mistakes.** Only actions on the same `step` run in parallel. A signer added to a running
   flow needs a `step` greater than or equal to the current pending step (PHP comment).
5. **Unwanted e-mails.** Creating a document notifies the first step unless you pass
   `disablePendingActionNotifications: true`. Pass it for embedded and API signing.
6. **Treating the create response as one document.** It is a list, with one item per file.
   Envelopes need `envelopeName` and PDF files only.
7. **A new version resets the flow**, and with it any signatures already collected.
8. **Folder lookup is a search.** The samples take the first match for a name, so check the name
   before using its `id`.
9. **Public signature.** The certificate's identifier (CPF/CNPJ) must match the participant's
   `identifier`. The PFX here is a test shortcut; in production the hash is signed on the
   user's device (smart card, token, browser).
10. **Copying a defect.** Read the catalog's "Caveat:" notes, for example on the Node.js webhook
    handler, which never matches an event type.

## 6. Conventions for adding or changing a sample

- **Reuse slugs.** Check `SAMPLES.md` for an existing task first. A new task is named
  `<task>-signer` and uses that slug in every language.
- Follow the project's structure: .NET `Console/Scenarios/<Name>Scenario.cs` (a `Scenario`
  subclass, listed in `Console.csproj`); Java `scenarios/<Name>Scenario.java` (a `Scenario`
  subclass); Node.js `scenarios/<name>Scenario.ts`, importing `config` from `./scenario`, opening
  files from `__dirname` and run with ts-node, with no new output in `dist`; PHP
  `Scenarios/<Name>Scenario.php` in namespace `Lacuna\Scenarios`, with a `use` line in `index.php`.
- **Every sample needs a catalog entry** in `samples.json`, one per language, with an `area` from
  `AREAS` in the generator. Then regenerate and validate; validation must pass:

  ```bash
  python scripts/generate-samples-md.py
  python scripts/validate-catalog.py
  ```

- `status`: `current` is recommended, `legacy` works but has a better alternative, and
  `deprecated` must not be copied. Change a status only after a maintainer confirms it.
- Never commit real credentials or real people's data, and leave the shared sample key alone.

## 7. Links

- Signer docs (English): https://docs.lacunasoftware.com/en-us/articles/signer. Portuguese is
  the default: https://docs.lacunasoftware.com/pt-br/articles/signer.
- Docs pages: [Getting started](https://docs.lacunasoftware.com/en-us/articles/signer/get-started),
  [Integration guide](https://docs.lacunasoftware.com/en-us/articles/signer/integration-guide),
  [Webhooks](https://docs.lacunasoftware.com/en-us/articles/signer/webhooks),
  [Embedded signature](https://docs.lacunasoftware.com/en-us/articles/signer/embedded-signature).
- API reference (Swagger): https://www.dropsigner.com/swagger
- Demo site:
  - [Sign a document](https://try.lacunasoftware.com/en/signer/sign-a-document)
    ([notes for agents](https://try.lacunasoftware.com/en/signer/sign-a-document.md))
  - [Sign without leaving your page](https://try.lacunasoftware.com/en/signer/embedded-signature)
    ([notes for agents](https://try.lacunasoftware.com/en/signer/embedded-signature.md))
- PKI Suite samples (same catalog format): https://github.com/LacunaSoftware/PkiSuiteSamples
- Client libraries: NuGet `Lacuna.Signer.Client`, Maven `signer-client`, npm `signer-node-client`.
  Composer: `lacuna/signer-client`.
