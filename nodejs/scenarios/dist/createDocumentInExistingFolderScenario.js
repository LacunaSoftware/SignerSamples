"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const signer_node_client_1 = require("signer-node-client");
const scenario_1 = require("./scenario");
/**
 * This scenario demonstrates the creation of a document into an existing folder.
 */
const uploadApi = new signer_node_client_1.UploadApi(scenario_1.config);
const foldersApi = new signer_node_client_1.FoldersApi(scenario_1.config);
const documentsApi = new signer_node_client_1.DocumentsApi(scenario_1.config);
const filePath = "../../samples/sample.pdf";
const fileName = "sample.pdf";
// 1. The file's bytes must be read by the application and uploaded
uploadApi
    .apiUploadsBytesPost({
    bytes: (0, signer_node_client_1.getBase64)(filePath),
})
    .then((res) => {
    // 2. Define the name of the document which will be visible in the application
    const fileUploadModel = {
        displayName: "New file in existing folder sample",
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
    // 4. Create a FlowActionCreateModel instance for each action (signature or approval) in the flow.
    //    This object is responsible for defining the personal data of the participant and the type of 
    //    action that he will perform on the flow
    const flowAction = {
        type: signer_node_client_1.FlowActionType.Signer,
        user: participant,
    };
    const files = [fileUploadModel];
    const flowActions = [flowAction];
    let folderId = "";
    // 5. Search a folder by it's name
    foldersApi.apiFoldersGet("Sample folder").then((res) => {
        console.log("Available items: ", res.data.items);
        folderId = res.data.items[0].id;
        // 6. Send the document create request setting the FolderId property
        if (folderId != null) {
            const documentRequest = {
                files: files,
                flowActions: flowActions,
                folderId: folderId,
            };
            // 7. Send the request
            documentsApi.apiDocumentsPost(documentRequest).then((response) => {
                console.log("Document: ", response.data[0], "created in folder", folderId);
            });
        }
    }, () => {
        console.log("Folder not found");
    });
}).catch(err => {
    console.error(err);
});
