"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const signer_node_client_1 = require("signer-node-client");
const scenario_1 = require("./scenario");
/**
 * This scenario demonstrates the creation of a document with description.
 */
const filePath = "../samples/sample.pdf";
const fileName = "sample.pdf";
const uploadApi = new signer_node_client_1.UploadApi(scenario_1.config);
const documentsApi = new signer_node_client_1.DocumentsApi(scenario_1.config);
// 1. The file's bytes must be read by the application and uploaded
uploadApi
    .apiUploadsBytesPost({
    bytes: (0, signer_node_client_1.getBase64)(filePath),
})
    .then((responseOne) => {
    uploadApi
        .apiUploadsBytesPost({
        bytes: (0, signer_node_client_1.getBase64)(filePath),
    })
        .then((responseTwo) => {
        // 2. Define the name of the document which will be visible in the application
        const fileUploadModelOne = {
            displayName: "Document sample",
            id: responseOne.data.id,
            contentType: "application/pdf",
            name: fileName,
        };
        const fileUploadModelTwo = {
            displayName: "Document sample",
            id: responseTwo.data.id,
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
        // 5. Send the document create request
        documentsApi
            .apiDocumentsPost({
            files: [fileUploadModelOne, fileUploadModelTwo],
            // 6. Set up the attribute "isEnvelope" as "true" and you MUST give a name to this envelope "envelopeName"
            isEnvelope: true,
            envelopeName: "Name",
            flowActions: [flowAction],
        })
            .then((res) => {
            // 7. Print result
            console.log("Document ", res.data[0].documentId, "created");
        });
    });
});
