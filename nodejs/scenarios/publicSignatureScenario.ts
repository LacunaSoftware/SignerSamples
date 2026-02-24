/**
 * Public Signature Scenario
 *
 * Similar to the C# sample: creates a document with one signer, gets the action URL,
 * then performs the signature using a local certificate (PFX) via the public
 * signature API (start → sign hash → complete), and prints the verification URL.
 *
 * Prerequisites:
 * - A PFX file for the signer (e.g. 81976153069.pfx for identifier 81976153069)
 * - PFX password if the file is protected (set pfxPassword below or leave empty)
 */

import * as crypto from "crypto";
import * as fs from "fs";
import * as path from "path";
import {
  DocumentsActionUrlRequest,
  DocumentsApi,
  DocumentsCreateDocumentRequest,
  FileUploadModel,
  FlowActionsFlowActionCreateModel,
  FlowActionType,
  getBase64,
  SignatureApi,
  UploadApi,
  UsersParticipantUserModel,
} from "signer-node-client";
// eslint-disable-next-line @typescript-eslint/no-var-requires
const forge = require("node-forge");

import { config } from "./scenario";

const uploadApi = new UploadApi(config);
const documentsApi = new DocumentsApi(config);
const signatureApi = new SignatureApi(config);
const basePath = config.basePath || "https://signer-lac.azurewebsites.net";

// User (signer) - must match the certificate subject/identifier
const user: UsersParticipantUserModel = {
  name: "Alan Turing",
  identifier: "56072386105",
  email: "alan.turing@mailinator.com",
};

// Paths: from scenarios/ (ts-node) use ../samples; from scenarios/dist/ (node) use ../../samples
const samplesDir = path.join(
  __dirname,
  ...(__dirname.endsWith("dist") ? ["..", "..", "samples"] : ["..", "samples"])
);
const pfxPath = path.join(samplesDir, `${user.identifier}.pfx`);

// Sample PDF to sign
const filepath = path.join(samplesDir, "sample.pdf");
const filename = "Sample PDF.pdf";

/**
 * Load certificate (as base64 DER) and private key from a PFX file.
 */
function loadCertificateAndKey(
  pfxPath: string
): { certificateBase64: string; privateKeyPem: string } {
  const pfxDer = fs.readFileSync(pfxPath, "binary");
  const pfxAsn1 = forge.asn1.fromDer(pfxDer);
  const pfx = forge.pkcs12.pkcs12FromAsn1(pfxAsn1, "1234");

  const certBags = pfx.getBags({ bagType: forge.pki.oids.certBag });
  const certBag = certBags[forge.pki.oids.certBag]?.[0];
  if (!certBag?.cert) {
    throw new Error("No certificate found in PFX");
  }
  const certAsn1 = forge.pki.certificateToAsn1(certBag.cert);
  const certDer = forge.asn1.toDer(certAsn1).getBytes();
  const certificateBase64 = Buffer.from(certDer, "binary").toString("base64");

  const keyBags = pfx.getBags({ bagType: forge.pki.oids.pkcs8ShroudedKeyBag });
  const keyBag = keyBags[forge.pki.oids.pkcs8ShroudedKeyBag]?.[0];
  if (!keyBag?.key) {
    throw new Error("No private key found in PFX");
  }
  const privateKeyPem = forge.pki.privateKeyToPem(keyBag.key);

  return { certificateBase64, privateKeyPem };
}

/** DigestInfo prefix for SHA-256 (DER): algorithm OID + OCTET STRING length 32. */
const SHA256_DIGEST_INFO_PREFIX = Buffer.from([
  0x30, 0x31, 0x30, 0x0d, 0x06, 0x09, 0x60, 0x86, 0x48, 0x01, 0x65, 0x03, 0x04,
  0x02, 0x01, 0x05, 0x00, 0x04, 0x20,
]);

/**
 * Equivalent to C# SignHash(certificate, toSignHash) with RSASignaturePadding.Pkcs1:
 *   key.SignHash(toSignHash, HashAlgorithmName.SHA256, RSASignaturePadding.Pkcs1)
 * The hash is passed as base64 string (decode to bytes, wrap in DigestInfo, sign with PKCS#1).
 * Returns the signature as base64 string.
 */
function signHash(privateKeyPem: string, toSignHashBase64: string): string {
  const hashBytes = Buffer.from(toSignHashBase64, "base64");
  if (hashBytes.length !== 32) {
    throw new Error(`Expected SHA-256 hash length 32, got ${hashBytes.length}`);
  }
  const digestInfo = Buffer.concat([
    SHA256_DIGEST_INFO_PREFIX,
    hashBytes,
  ] as unknown as readonly Uint8Array[]);
  const key = crypto.createPrivateKey(privateKeyPem);
  const signature = crypto.privateEncrypt(
    {
      key,
      padding: crypto.constants.RSA_PKCS1_PADDING,
    },
    digestInfo as unknown as NodeJS.ArrayBufferView
  );
  return signature.toString("base64");
}
/**
 * Public signature APIs are not in signer-node-client; we call them with axios.
 * Adjust the paths if your Signer instance uses different routes.
 */
async function startPublicSignature(
  documentKey: string,
  ticket: string,
  certificateBase64: string,
): Promise<{ token: string; toSignHash: string }> {
  const { data } = await signatureApi.startPublicSignatureAsync(documentKey, { ticket, certificate: certificateBase64 });
  return { token: data.token, toSignHash: data.toSignHash };
}

async function completePublicSignature(
  documentKey: string,
  token: string,
  signatureBase64: string
): Promise<void> {
  await signatureApi.completePublicSignatureAsync(documentKey, { token, signature: signatureBase64 });
}

// function validateDigitalSignature(data: string, receivedSignature: string, certificate: string): boolean {
//   const verify = crypto.createVerify('RSA-SHA256');
//   verify.update(data);
//   return verify.verify(certificate, receivedSignature, 'base64');
// }

async function run(): Promise<void> {
  // #1: Uploading file
  const uploadRes = await uploadApi.apiUploadsBytesPost({
    bytes: getBase64(filepath),
  });
  const uploadId = uploadRes.data.id;

  const fileUpload: FileUploadModel = {
    id: uploadId,
    name: filename,
    contentType: "application/pdf",
    displayName: "Sample PDF",
  };

  const flowAction: FlowActionsFlowActionCreateModel = {
    type: FlowActionType.Signer,
    user,
  };

  const documentRequest: DocumentsCreateDocumentRequest = {
    files: [fileUpload],
    flowActions: [flowAction],
  };

  // #2: Creating document
  const createRes = await documentsApi.apiDocumentsPost(documentRequest);
  const documentId = createRes.data[0].documentId;

  // #3: Getting action URL
  const actionUrlRequest: DocumentsActionUrlRequest = {
    emailAddress: user.email ?? undefined,
    identifier: user.identifier ?? undefined,
  };
  const actionUrlRes = await documentsApi.apiDocumentsIdActionUrlPost(
    documentId,
    actionUrlRequest
  );
  const actionUrl = actionUrlRes.data.url;
  if (!actionUrl) {
    throw new Error("No action URL returned");
  }

  const signatureUrl = new URL(actionUrl);
  const pathMatch = signatureUrl.pathname.match(/\/document\/key\/([^/]+)\/sign/);
  if (!pathMatch) {
    throw new Error(`Could not parse document key from action URL: ${actionUrl}`);
  }
  const documentKey = pathMatch[1];
  const ticket = signatureUrl.searchParams.get("ticket") ?? "";
  if (!ticket) {
    throw new Error("No ticket in action URL");
  }

  // #4: Loading certificate from PFX
  const { certificateBase64, privateKeyPem } = loadCertificateAndKey(
    pfxPath
  );

  // #5: Starting public signature
  const startRes = await startPublicSignature(
    documentKey,
    ticket,
    certificateBase64
  );
  

  // #6: Signing hash with certificate private key
  const signature = signHash(privateKeyPem, startRes.toSignHash);
  const signatureBase64 = signature;

  // const isValid = validateDigitalSignature(startRes.toSignHash, signatureBase64, certificateBase64);
  // if (!isValid) {
  //   throw new Error("Invalid signature");
  // }

  // #7: Completing public signature
  await signatureApi.completePublicSignatureAsync(
    documentKey,
    { token: startRes.token, signature: signatureBase64 },
  );

  const verificationUrl = `${basePath.replace(/\/$/, "")}/validate/${documentKey}`;
  console.log("\nVerification URL is:");
  console.log(`\n${verificationUrl}\n`);
  console.log("Generate a QR code from the URL above");
}

run().catch((err) => {
  console.error(err);
  process.exit(1);
});
