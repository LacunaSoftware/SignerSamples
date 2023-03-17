"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const signer_node_client_1 = require("signer-node-client");
const scenario_1 = require("./scenario");
/**
 * This scenario demonstrates how to refuse a document as an organization application.
 */
const docApi = new signer_node_client_1.DocumentsApi(scenario_1.config);
(0, scenario_1.CreateDocument)().then((res) => {
    //1. You need a document id
    const docId = res.documentId;
    //2 - Create a refusal request and give it a reason
    const refuseDocument = {
        reason: "This is a document refusal"
    };
    //3 - Send the refusal request
    docApi.apiDocumentsIdRefusalPost(docId, refuseDocument).then((response) => {
        console.log(response.status);
    });
});
