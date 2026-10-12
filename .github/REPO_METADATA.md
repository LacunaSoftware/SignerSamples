# Repository metadata (to apply by hand)

These GitHub settings cannot be set from files in the repository. A maintainer with admin rights
applies them once, in **Settings → General** (description and website) and in the **About** panel
(topics), or with the `gh` command at the end of this file. On 2026-10-12 the repository had no
description, no website and no topics.

## Description

> Sample console apps for Lacuna Signer (Dropsigner): create documents and signing flows, embed the signing page, handle webhooks and download signed files, in .NET, Java, Node.js and PHP.

## Website

`https://docs.lacunasoftware.com/en-us/articles/signer`

Use it **without** a trailing slash. On 2026-10-12,
`https://docs.lacunasoftware.com/en-us/articles/signer/` returned 404, while the URL above
returned 200.

## Topics (11)

`electronic-signature`, `digital-signature`, `document-signing`, `e-signature`, `webhooks`,
`dotnet`, `java`, `nodejs`, `php`, `lacuna-signer`, `dropsigner`

GitHub allows up to 20 topics. If you want more, good candidates are `typescript`, `xades`,
`cades`, `icp-brasil` and `code-samples`.

## Apply with the GitHub CLI

```bash
gh repo edit LacunaSoftware/SignerSamples --description "Sample console apps for Lacuna Signer (Dropsigner): create documents and signing flows, embed the signing page, handle webhooks and download signed files, in .NET, Java, Node.js and PHP." --homepage "https://docs.lacunasoftware.com/en-us/articles/signer" --add-topic electronic-signature,digital-signature,document-signing,e-signature,webhooks,dotnet,java,nodejs,php,lacuna-signer,dropsigner
```
