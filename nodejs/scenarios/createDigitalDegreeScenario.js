"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const signer_node_client_1 = require("signer-node-client");
const scenario_1 = require("./scenario");
const filePath = "../samples/sample-degree.xml";
const filename = "sample-degree.xml";
const file = (0, signer_node_client_1.getBase64)(filePath);
const uploadApi = new signer_node_client_1.UploadApi(scenario_1.config);
const documentApi = new signer_node_client_1.DocumentsApi(scenario_1.config);
// 1. The file's bytes must be read by the application and uploaded
uploadApi.apiUploadsBytesPost({
    bytes: file,
})
    .then((res) => {
    // 2. Define the name of the document which will be visible in the application
    const fileUploadModel = {
        contentType: "application/xml",
        displayName: "Digital Degree Sample",
        id: res.data.id,
        name: filename,
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
    const participantUserThree = {
        name: "Garry Eggsy",
        email: "garry.eggsy@mailinator.com",
        identifier: "87657257008",
    };
    // 4. Specify the element that holds the namespace of the issuer
    const xmlNamespaceModel = {
        prefix: "dip",
        uri: "http://portal.mec.gov.br/diplomadigital/arquivos-em-xsd",
    };
    // 5. The fields 'DadosDiploma' and 'DadosRegistro' and the entire XML file must be signed
    const xadesOptionsDegreeData = {
        signatureType: signer_node_client_1.XadesSignatureTypes.XmlElement,
        elementToSignIdentifierType: signer_node_client_1.XadesElementIdentifierTypes.XPath,
        elementToSignIdentifier: "//dip:DadosDiploma",
        insertionOption: signer_node_client_1.XadesInsertionOptions.AppendChild,
    };
    const xadesOptionsModelRegisterData = {
        signatureType: signer_node_client_1.XadesSignatureTypes.XmlElement,
        elementToSignIdentifierType: signer_node_client_1.XadesElementIdentifierTypes.XPath,
        elementToSignIdentifier: "//dip:DadosRegistro",
        insertionOption: signer_node_client_1.XadesInsertionOptions.AppendChild,
    };
    const xadesOptionsModelFull = {
        signatureType: signer_node_client_1.XadesSignatureTypes.FullXml
    };
    // 6. Each signature requires its own flow action
    const degreeDataAction = {
        type: signer_node_client_1.FlowActionType.Signer,
        user: participantUserOne,
        xadesOptions: xadesOptionsDegreeData
    };
    const registerDataAction = {
        type: signer_node_client_1.FlowActionType.Signer,
        user: participantUserTwo,
        xadesOptions: xadesOptionsModelRegisterData
    };
    const flowActionCreateModelFull = {
        type: signer_node_client_1.FlowActionType.Signer,
        user: participantUserThree,
        xadesOptions: xadesOptionsModelFull
    };
    // 7. Send the document create request
    const documentRequest = {
        newFolderName: "New folder",
        files: [fileUploadModel],
        flowActions: [degreeDataAction, registerDataAction, flowActionCreateModelFull],
        xmlNamespaces: [xmlNamespaceModel]
    };
    documentApi.apiDocumentsPost(documentRequest).then((res) => {
        console.log("Document id: ", res.data[0].documentId);
    });
});
