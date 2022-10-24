"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const signer_node_client_1 = require("signer-node-client");
const scenario_1 = require("./scenario");
const uploadApi = new signer_node_client_1.UploadApi(scenario_1.config);
const documentsApi = new signer_node_client_1.DocumentsApi(scenario_1.config);
const filepath = "../samples/sample.pdf";
const filename = "sample.pdf";
// 1. The file's bytes must be read by the application and uploaded
uploadApi.apiUploadsBytesPost({ bytes: (0, signer_node_client_1.getBase64)(filepath) }).then((res) => {
    // 2. Define the name of the document which will be visible in the application
    const uploadModel = {
        id: res.data.id,
        name: filename,
        contentType: "application/pdf",
        displayName: "Cades Signature Sample",
    };
    // 3. For each participant on the flow, create one instance of ParticipantUserModel
    const participant = {
        name: "Jack Bauer",
        email: "jack.bauer@mailinator.com",
        identifier: "75502846369",
    };
    // 4. Create a FlowActionCreateModel instance for each action (signature or approval) in the flow.
    //    This object is responsible for defining the personal data of the participant and the type of
    //    action that he will perform on the flow
    const flowAction = {
        type: signer_node_client_1.FlowActionType.Signer,
        user: participant,
    };
    // 5. Send the document create request
    const documentRequest = {
        files: [uploadModel],
        flowActions: [flowAction],
        // 6. This time we'll add the forceCadesSignature parameter
        forceCadesSignature: true,
    };
    documentsApi.apiDocumentsPost(documentRequest).then((res) => {
        console.log("Document ", res.data[0].documentId, " Created");
    });
});
