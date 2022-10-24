import {
  DocumentsApi,
  FileUploadModel,
  UploadApi,
  DocumentsDocumentAddVersionRequest,
} from "signer-node-client";
import { config, CreateDocument } from "./scenario";
import { getBase64 } from "signer-node-client";

const uploadApi = new UploadApi(config);
const documentsApi = new DocumentsApi(config);

CreateDocument().then((res) => {
  // 1. Retrieve document id
  const docId = res.documentId;

  // 2. The file's bytes must be read by the application and uploaded
  const filepath = "..\\samples\\sample.pdf";
  const fileName = "sample.pdf";
  const base64File = getBase64(filepath);

  uploadApi
    .apiUploadsBytesPost({
      bytes: base64File,
    })
    .then((response) => {
      // 3. Define the name of the document which will be visible in the application
      const fileUploadModel: FileUploadModel = {
        name: fileName,
        displayName: "Add New Document Version Sample",
        contentType: "application/pdf",
        id: response.data.id,
      };
      // 4. Create object and send the new version request
      const documentAddVersionRequest: DocumentsDocumentAddVersionRequest = {
        file: fileUploadModel,
      };
      documentsApi
        .apiDocumentsIdVersionsPost(docId, documentAddVersionRequest)
        .then((response) => {
          console.log(response.status);
        });
    });
});
