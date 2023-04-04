"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const signer_node_client_1 = require("signer-node-client");
const scenario_1 = require("./scenario");
/**
 * This scenario demonstrates the creation of a document with description.
 */
const filePath = "../../samples/sample.pdf";
const fileName = "sample.pdf";
const uploadApi = new signer_node_client_1.UploadApi(scenario_1.config);
const documentsApi = new signer_node_client_1.DocumentsApi(scenario_1.config);
// 1. The file's bytes must be read by the application and uploaded
uploadApi
    .apiUploadsBytesPost({
    bytes: (0, signer_node_client_1.getBase64)(filePath),
})
    .then((res) => {
    // 2. Define the name of the document which will be visible in the application
    const fileUploadModel = {
        displayName: "Document sample",
        id: res.data.id,
        contentType: "application/pdf",
        name: fileName,
    };
    // 3. For each participant on the flow, create one instance of ParticipantUserModel
    const participant = {
        name: "Jack Bauer",
        email: "jack.bauer@mailnator.com",
        identifier: "75502846369",
    };
    // 4. Create a FlowActionCreateModel instance for each action (signature or approval) in the flow
    const flowAction = {
        type: signer_node_client_1.FlowActionType.Signer,
        user: participant,
    };
    const description = "One Description Sample";
    // 5. Send the document create request
    documentsApi
        .apiDocumentsPost({
        files: [fileUploadModel],
        // 6. The description goes here
        description: description,
        flowActions: [flowAction],
    })
        .then((res) => {
        // 7. Print result
        console.log("Document ", res.data[0].documentId, "created");
    });
});
