import {
  DocumentFilterStatus,
  DocumentsApi,
  PaginationOrders,
} from "signer-node-client";
import { config } from "./scenario";

/**
 * This scenario describes how to list documents (GET /api/documents).
 */

const documentsApi = new DocumentsApi(config);

// 1 - Define the arguments to list the documents according to your requirements
const status = DocumentFilterStatus.Pending; // List documents according to their status (isConcluded is deprecated)
const q = "TestDocumentName"; // Retrieve the document list by name
const limit = 10; // Define the number of documents that you want to list
const offset = 0;
const order = PaginationOrders.Desc; // Order the list: Asc = oldest on top / Desc = newest on top

// 2 - Call apiDocumentsGet. Its parameters are positional, not inside an interface: pass undefined
// for the filters you don't use (isConcluded comes first, status second, and q, limit, offset and
// order last).
documentsApi
  .apiDocumentsGet(
    undefined, // isConcluded (deprecated, use status)
    status,
    undefined, // folderId
    undefined, // folderType
    undefined, // documentType
    undefined, // filterByDocumentType
    undefined, // filterByPendingSignature
    undefined, // queryType
    undefined, // participantQ
    undefined, // participantQueryType
    undefined, // tags
    undefined, // isDeleted
    q,
    limit,
    offset,
    order
  )
  .then((response) => {
    console.log(`${response.data.totalCount} document(s) found`);
    for (const document of response.data.items ?? []) {
      console.log(`${document.id} ${document.name}`);
    }
  });
