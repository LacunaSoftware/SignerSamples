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
        displayName: "Signing Rule Sample",
    };
    // 3. For each participant on the flow, create one instance of ParticipantUserModel
    const participantUserOne = {
        name: "Jack Bauer",
        email: "jack.bauer@mailinator.com",
        identifier: "75502846369",
    };
    const participantUserTwo = {
        name: "James Bond",
        email: "james.bond@mailinator.com",
        identifier: "95588148061",
    };
    // 4. Each signing rule requires just one FlowActionCreateModel no matter
    //    the number of participants assigned to it. The participants are assigned to
    //    it via a list of ParticipantUserModel assigned to the `SignRuleUsers` property.
    //    The number of required signatures from this list of participants is represented by
    //    the property `NumberRequiredSignatures`.
    const flowAction = {
        type: signer_node_client_1.FlowActionType.SignRule,
        numberRequiredSignatures: 1,
        signRuleUsers: [participantUserOne, participantUserTwo],
    };
    // 5. Send the document create request
    const documentRequest = {
        files: [uploadModel],
        flowActions: [flowAction],
    };
    documentsApi.apiDocumentsPost(documentRequest).then((res) => {
        console.log("Document ", res.data[0].documentId, " Created");
    });
});
