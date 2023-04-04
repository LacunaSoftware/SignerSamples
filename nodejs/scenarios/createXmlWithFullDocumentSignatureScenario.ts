import {
    DocumentsApi,
    FileUploadModel,
    UploadApi,
    UsersParticipantUserModel,
    FlowActionsFlowActionCreateModel,
    FlowActionType,
    DocumentsCreateDocumentRequest,
    FlowActionsXadesOptionsModel,
    XadesSignatureTypes,
    getBase64,
  } from "signer-node-client";
  import { config } from "./scenario";
  
  const uploadApi = new UploadApi(config);
  const documentsApi = new DocumentsApi(config);
  const filepath = "../../samples/sample.xml";
  const filename = "sample.xml";
  
  // 1. The file's bytes must be read by the application and uploaded
  uploadApi.apiUploadsBytesPost({ bytes: getBase64(filepath) }).then((res) => {
    // 2. Define the name of the document which will be visible in the application
    const uploadModel: FileUploadModel = {
      id: res.data.id,
      name: filename,
      contentType: "application/xml",
      displayName: "XML Full Signature Sample",
    };
    // 3. For each participant on the flow, create one instance of ParticipantUserModel
    const participant: UsersParticipantUserModel = {
      name: "Jack Bauer",
      email: "jack.bauer@mailinator.com",
      identifier: "75502846369",
    };
  
    // 4. Specify the signature type
    const xadesOptionsModel: FlowActionsXadesOptionsModel = {
      signatureType: XadesSignatureTypes.FullXml
    };
  
    // 5. Create a FlowActionCreateModel instance for each action (signature or approval) in the flow.
    //    This object is responsible for defining the personal data of the participant and the type of
    //    action that he will perform on the flow.
    const flowAction: FlowActionsFlowActionCreateModel = {
      type: FlowActionType.Signer,
      user: participant,
      xadesOptions: xadesOptionsModel,
    };

    // 6. Create and send the document request
    const documentRequest: DocumentsCreateDocumentRequest = {
      files: [uploadModel],
      flowActions: [flowAction],
    };
    documentsApi.apiDocumentsPost(documentRequest).then((res) => {
      console.log("Document ", res.data[0].documentId, "created");
    });
  });
  