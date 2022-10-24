import {
  DocumentsApi,
  DocumentsCancelDocumentRequest,
} from "signer-node-client";
import { config, CreateDocument } from "./scenario";

const documentsApi = new DocumentsApi(config);

// 1 - You need a document id
CreateDocument().then((res) => {
  const documentId = res.documentId;

  //2 - Create a cancellation request and give it a reason
  const cancelDocument: DocumentsCancelDocumentRequest = {
    reason: "This is a document cancellation",
  };

  //3 - Send the cancellation request

  documentsApi
    .apiDocumentsIdCancellationPost(documentId, cancelDocument)
    .then((response) => {
      console.log(response.status);
    });
});
