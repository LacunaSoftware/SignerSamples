import {
  DocumentDownloadTypes,
  DocumentsApi,
  DocumentStatus,
  DocumentTicketType,
} from "signer-node-client";
import { config, CreateDocument } from "./scenario";
import * as fs from "fs";
import * as path from "path";

// Signatures and PrinterFriendlyVersion exist only after at least one participant has signed.
// To download them, set this to the id of a signed document of your organization, for instance
// one whose status is Concluded. While it is null, the scenario creates a new document, which
// nobody has signed yet, and downloads only its Original version.
const signedDocumentId: string | null = null;

const documentsApi = new DocumentsApi(config);

async function run(): Promise<void> {
  // 1. Get a document Id
  const docId = signedDocumentId ?? (await CreateDocument()).documentId;

  //2. You can get a ticket to a specific version of the document. The ticket is a temporary URL (valid for 1 hour,
  //   no authentication needed) that allows you to download that version. Anyone with the URL can download it.
  const ticketDownload = (
    await documentsApi.apiDocumentsIdTicketGet(
      docId,
      DocumentTicketType.Original
    )
  ).data;

  // 3. Get the document by passing its Id and the version type.
  // Be sure to select the exact DocumentDownloadTypes to download the type of document you want.
  // Original: the file uploaded when the document was created. It is always available.
  await saveContent(docId, DocumentDownloadTypes.Original, "downloaded-original.pdf");

  // 4. You can also get the bytes directly, encoded in Base64, for a specific version type of the document
  const documentVersionBytes = (
    await documentsApi.apiDocumentsIdContentB64Get(
      docId,
      DocumentDownloadTypes.Original
    )
  ).data;

  // 5. For a document that nobody has signed yet, Signatures and PrinterFriendlyVersion fail with
  //    422 and the code DocumentHasNoSignatures. Read the document first.
  const details = (await documentsApi.apiDocumentsIdGet(docId)).data;
  if (!details.hasSignature) {
    console.log(`Document ${docId} has no signatures yet, so only its Original version was downloaded.`);
    return;
  }

  // 6. Signatures: the signed file. For a PDF signed with PAdES (the default) it is the PDF with the signatures
  //    embedded; for a CAdES signature it is a .p7s file. It holds the signatures collected so far: the document
  //    is final only when its status is Concluded. Canceled, refused and expired documents download only as
  //    Original and PrinterFriendlyVersion.
  if (
    details.status === DocumentStatus.Canceled ||
    details.status === DocumentStatus.Refused ||
    details.status === DocumentStatus.Expired
  ) {
    console.log(`Document ${docId} is ${details.status}, so its signed file cannot be downloaded.`);
  } else {
    await saveContent(docId, DocumentDownloadTypes.Signatures, "downloaded-signed.pdf");
  }

  // 7. PrinterFriendlyVersion: a PDF with the signature marks and a signatures manifest appended.
  await saveContent(docId, DocumentDownloadTypes.PrinterFriendlyVersion, "downloaded-printer-friendly.pdf");
}

// Downloads one version and saves it to nodejs/samples. responseType "arraybuffer" makes axios return
// the file's bytes unchanged; without it, axios decodes the content as text and corrupts binary files.
async function saveContent(docId: string, type: DocumentDownloadTypes, fileName: string): Promise<void> {
  const response = await documentsApi.apiDocumentsIdContentGet(docId, type, {
    responseType: "arraybuffer",
  });
  const filePath = path.join(__dirname, "..", "samples", fileName);
  fs.writeFileSync(filePath, Buffer.from(response.data as unknown as ArrayBuffer));
  console.log(`Saved ${filePath}`);
}

run().catch((err) => {
  console.error(err);
  process.exit(1);
});
