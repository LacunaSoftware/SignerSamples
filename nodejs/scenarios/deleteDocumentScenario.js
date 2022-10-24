"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const signer_node_client_1 = require("signer-node-client");
const scenario_1 = require("./scenario");
const documentsApi = new signer_node_client_1.DocumentsApi(scenario_1.config);
(0, scenario_1.CreateDocument)().then((res) => {
    //1 - You need a document id
    const docId = res.documentId;
    //2 - Call the api method to delete the document and pass the document Id as parameter
    documentsApi.apiDocumentsIdDelete(docId).then((res) => {
        if (res.status === 200) {
            console.log("Document", docId, "deleted");
        }
    });
});
