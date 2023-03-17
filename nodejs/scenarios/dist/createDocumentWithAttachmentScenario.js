"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const signer_node_client_1 = require("signer-node-client");
const scenario_1 = require("./scenario");
/**
 * This scenario demonstrates the creation of a document with Attachment.
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
    .then((res) => {
    // 2. Define the name of the document which will be visible in the application
    const fileUploadModel = {
        displayName: "Document sample",
        id: res.data.id,
        contentType: "application/pdf",
        name: fileName,
    };
    // 3. Repeat the same steps above but now for the attachment and using AttachmentUploadBuilder
    uploadApi
        .apiUploadsBytesPost({
        bytes: (0, signer_node_client_1.getBase64)(filePath),
    })
        .then((res) => {
        const attachmentFileUploadModelBuilder = {
            id: res.data.id,
            name: fileName,
            contentType: "application/pdf",
            displayName: "One Attachment Sample",
            //here you can define if the attachment will be restrict to the organization participants (true) or to everyone in the document flow (false)
            isPrivate: false,
        };
        // 4. For each participant on the flow, create one instance of ParticipantUserModel
        const participant = {
            name: "Jack Bauer",
            email: "jack.bauer@mailnator.com",
            identifier: "75502846369",
        };
        // 5. Create a FlowActionCreateModel instance for each action
        const flowAction = {
            type: signer_node_client_1.FlowActionType.Signer,
            user: participant,
        };
        // 6. Send the document create request with attachment's attribute
        documentsApi
            .apiDocumentsPost({
            files: [fileUploadModel],
            // set the attachment
            attachments: [attachmentFileUploadModelBuilder],
            flowActions: [flowAction],
        })
            .then((res) => {
            // 7. Print result
            console.log("Document ", res.data[0].documentId, "created");
        });
    });
});
