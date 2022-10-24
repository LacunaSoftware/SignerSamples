"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const signer_node_client_1 = require("signer-node-client");
const scenario_1 = require("./scenario");
const uploadApi = new signer_node_client_1.UploadApi(scenario_1.config);
const documentsApi = new signer_node_client_1.DocumentsApi(scenario_1.config);
const filepath = "../samples/sample.xml";
const filename = "sample.xml";
// 1. The file's bytes must be read by the application and uploaded
uploadApi.apiUploadsBytesPost({ bytes: (0, signer_node_client_1.getBase64)(filepath) }).then((res) => {
    // 2. Define the name of the document which will be visible in the application
    const uploadModel = {
        id: res.data.id,
        name: filename,
        contentType: "application/xml",
        displayName: "XML Element Sign Sample",
    };
    // 3. For each participant on the flow, create one instance of ParticipantUserModel
    const participant = {
        name: "Jack Bauer",
        email: "jack.bauer@mailinator.com",
        identifier: "75502846369",
    };
    // 4. Specify the type of the element (Id is used below) and the value of the identifier
    const xadesOptionsModel = {
        signatureType: signer_node_client_1.XadesSignatureTypes.XmlElement,
        elementToSignIdentifierType: signer_node_client_1.XadesElementIdentifierTypes.Id,
        elementToSignIdentifier: "NFe35141214314050000662550010001084271182362300",
    };
    // 5. Create a FlowActionCreateModel instance for each action (signature or approval) in the flow.
    //    This object is responsible for defining the personal data of the participant and the type of
    //    action that he will perform on the flow.
    const flowAction = {
        type: signer_node_client_1.FlowActionType.Signer,
        user: participant,
        xadesOptions: xadesOptionsModel,
    };
    // 6. Create and send the document request
    const documentRequest = {
        files: [uploadModel],
        flowActions: [flowAction],
    };
    documentsApi.apiDocumentsPost(documentRequest).then((res) => {
        console.log("Document ", res.data[0].documentId, "created");
    });
});
