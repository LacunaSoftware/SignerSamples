# Skill-friendly signature app (.NET 10 console)

A command-line app that lists the documents pending signature for a participant on **Lacuna
Signer** (DropSigner by default) and signs them with a certificate from the Windows certificate
store, through **PKI SDK**. It is built to be called by a coding agent: it prints JSON, never
prompts on the console (a smart card driver may still show its own PIN dialog), and ships a
Claude Code skill ([`skill/SKILL.md`](skill/SKILL.md)) that drives it.

It differs from the scenario projects in this repository on purpose:

- It is a tool with commands, not one scenario per run. A coding agent calls commands and reads
  their output, so the app never prompts: JSON on stdout, JSON errors on stderr, and one exit
  code per failure class.
- It signs the user's own documents, so it targets DropSigner by default, not the sandbox, and
  ships no API key. It reads `Signer:Endpoint` and `Signer:ApiKey` from its own configuration
  (below), not `SIGNER_ENDPOINT` and `SIGNER_API_KEY`, and never from a command-line flag, so the
  key never appears in the agent's commands.
- A signature is legally binding, so an agent cannot sign on its own: `sign` takes explicit
  document ids and has no "sign everything" option, and the skill asks the user to confirm the
  listed documents before it calls `sign`.
- It uses **PKI SDK** (NuGet `Lacuna.Pki`) for the Windows certificate store and the hash
  signature, so `certs` and `sign` run on Windows only.
- It has unit and command-line contract tests, which CI runs
  (`.github/workflows/test-skill-friendly-signature-app.yml`).

## Samples in this project

Every row is an entry in the repository catalog ([samples.json](../../../samples.json),
[SAMPLES.md](../../../SAMPLES.md)).

<!-- BEGIN GENERATED SAMPLES LIST (project: dotnet/SkillFriendlySignatureApp/SkillFriendlySignatureApp) -->
| Sample | Area | Entry file | Status |
|---|---|---|---|
| Sign your pending Signer documents from a Claude Code skill<br>`sign-pending-documents` | Sign through the API | [SignCommand.cs](Commands/SignCommand.cs) | `current` |
<!-- END GENERATED SAMPLES LIST -->

## How it signs

The private key never leaves the machine. For each document, `sign`:

1. gets the participant's action URL from Signer, which carries a public signature key and
   ticket (an organization API key cannot sign as a Signer user);
2. starts the signature on Signer with the certificate, which returns the hash to sign;
3. signs that hash with the private key, locally, through PKI SDK;
4. completes the signature on Signer.

The participant's CPF comes from the certificate, because Signer refuses a certificate whose CPF
is not the participant's (`CpfMismatch`). A **pending document** is one the participant can sign
with a certificate right now: approvals, and steps whose turn has not come yet, are left out.

## Configure

Requires Windows and the .NET 10 SDK. Settings are read from `appsettings.json`, then user
secrets, then environment variables (`Signer__ApiKey`). Every key is documented in
[`appsettings.json.example`](appsettings.json.example).

- **Signer API key** (required for `list` and `sign`): create an application on your Signer
  organization and keep its key out of the repository:

  ```bash
  dotnet user-secrets set Signer:ApiKey "<app name>|<key>"
  ```

- **Signer endpoint**: `Signer:Endpoint`, `https://www.dropsigner.com` by default. The sandbox is
  `https://signer-lac.azurewebsites.net`. An API key works only on the instance that issued it.
- **PKI SDK license**: `PkiSdk:License` (or `PkiSdk__License`) with a Base64 license. Without
  it, the app loads `LacunaPkiLicense.config` next to the executable. The committed one is a
  short-lived trial license; when it expires, ask Lacuna for a PKI SDK license.

Pass none of these as command-line flags: an agent that runs the app would see them.

## Run

```bash
dotnet run -- certs
dotnet run -- list --cpf 560.723.861-05
dotnet run -- sign --thumbprint <thumbprint> --id <document id> --id <document id>
dotnet run -- --help
```

| Command | Prints |
|---|---|
| `certs` | Certificates in `CurrentUser\My` with a private key, valid now, carrying a CPF |
| `list --cpf <cpf>` / `list --email <email>` | The participant's pending documents, with their ids |
| `sign --thumbprint <t> --id <id>... [--email <e>]` | One result per document: `signed`, or `failed`, `unknown` or `notAttempted` with the error |

Errors go to stderr as `{"error":{"code","message"}}`. Exit codes: 0 success, 1 not every
document was signed, 2 invalid arguments, 3 configuration, 4 certificate, 5 Signer error (from
`list`; `sign` reports Signer errors per document), 6 canceled, 10 unexpected error.

`sign` accounts for every document, because a signature cannot be undone:

- **`unknown`**: Signer did not answer the last step (a timeout or a dropped connection), and
  asking again could not confirm that the document was signed. Run `list` again: the document
  is signed if it is no longer pending.
- **`notAttempted`**: Ctrl+C stops between documents, never in the middle of one, and still
  prints the results. An error that every document would hit, such as a refused API key or a
  wrong PIN, also stops the run, so that a smart card does not lock after a few wrong PINs.

## Use it from Claude Code

1. Publish the app once, so the skill calls an executable instead of rebuilding on every call:

   ```bash
   dotnet publish -c Release -o C:/Tools/SkillFriendlySignatureApp
   ```

2. Copy `skill/` to `.claude/skills/sign-pending-documents/` in your project (or to
   `~/.claude/skills/sign-pending-documents/` for every project).
3. Edit the `APP=` line at the top of the copied `SKILL.md` if you published elsewhere.
4. Ask Claude "what do I have to sign on DropSigner?". The skill lists your documents and signs
   only the ones you confirm.

## Tests

```bash
cd ..
dotnet test --solution SkillFriendlySignatureApp.slnx
```

The unit and command-line contract tests use an in-memory Signer and `Pierre de Fermat.pfx`.
CI runs them on every pull request that touches `dotnet/SkillFriendlySignatureApp`
(`.github/workflows/test-skill-friendly-signature-app.yml`). `LiveSandboxTests` run against the Signer sandbox, on
Windows, only when `Signer__ApiKey` is set (to a sandbox API key: the tests do not check) and the
Windows store has a Lacuna test certificate (issued by *Lacuna CA Test v7*, with a
CPF), or `SkillSignatureTests__Thumbprint` names one. They create documents there and leave them.
