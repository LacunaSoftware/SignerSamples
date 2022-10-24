import { DocumentsApi } from "signer-node-client";
import { config, CreateDocument } from "./scenario";

const documentsApi = new DocumentsApi(config)

CreateDocument().then((res) => {
    //1 - You need a document id
    const docId = res.documentId;

    //2 - Call the api method to delete the document and pass the document Id as parameter
    documentsApi.apiDocumentsIdDelete(docId).then((res) => {
        if(res.status === 200){
            console.log("Document", docId, "deleted");
        }
    })
});