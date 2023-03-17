"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const signer_node_client_1 = require("signer-node-client");
const scenario_1 = require("./scenario");
const documentsApi = new signer_node_client_1.DocumentsApi(scenario_1.config);
// 1 - You need a document id
(0, scenario_1.CreateDocument)().then((res) => {
    const documentId = res.documentId;
    //2 - Create a cancellation request and give it a reason
    const cancelDocument = {
        reason: "This is a document cancellation",
    };
    //3 - Send the cancellation request
    documentsApi
        .apiDocumentsIdCancellationPost(documentId, cancelDocument)
        .then((response) => {
        console.log(response.status);
    });
});
