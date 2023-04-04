import {
  DocumentDownloadTypes,
  DocumentsApi,
  DocumentTicketType,
} from "signer-node-client";
import { config, CreateDocument } from "./scenario";
import * as fs from "fs";

const documentsApi = new DocumentsApi(config);
CreateDocument().then(async (res) => {
  // 1. Get a document Id
  const docId = res.documentId;
  //2. You can get a ticket to a specific version of the document. The ticket is a temporary URL that allows you to download that version.
  const ticketDownload = (
    await documentsApi.apiDocumentsIdTicketGet(
      docId,
      DocumentTicketType.Original
    )
  ).data;

  // 3. Get the document by passing it's Id and the Ticket type
  // Be sure to select the exact DocumentTicketType to download the type of document you want.
  // Check the available types by inspecting DocumentTicketType's ENUM.
  const documentVersion = (
    await documentsApi.apiDocumentsIdContentGet(
      docId,
      DocumentDownloadTypes.Original
    )
  ).data;
  saveFileStream(documentVersion);

  // 4. You can also get the bytes directly instead of a Stream for a specific version type of the document
  const documentVersionBytes = (
    await documentsApi.apiDocumentsIdContentB64Get(
      docId,
      DocumentDownloadTypes.Original
    )
  ).data;
});

function saveFileStream(stream) {
  fs.writeFileSync("../../samples/sample-copy.pdf", stream, {
    encoding: "utf8",
    flag: "w",
  });
}
