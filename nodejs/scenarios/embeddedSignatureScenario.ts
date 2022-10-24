import {
    DocumentsApi,
    FileUploadModel,
    UploadApi,
    UsersParticipantUserModel,
    FlowActionsFlowActionCreateModel,
    FlowActionType,
    DocumentsCreateDocumentRequest,
    DocumentsActionUrlRequest,
    getBase64,
  } from "signer-node-client";
  import { config } from "./scenario";

  const uploadApi = new UploadApi(config);
  const documentsApi = new DocumentsApi(config);
  const filepath = "../samples/sample.pdf";
  const filename = "sample.pdf";

  // 1. The file's bytes must be read by the application and uploaded
  uploadApi.apiUploadsBytesPost({ bytes: getBase64(filepath) }).then((res) => {
    // 2. Define the name of the document which will be visible in the application
    const uploadModel: FileUploadModel = {
      id: res.data.id,
      name: filename,
      contentType: "application/pdf",
      displayName: "Embedded Signature Sample",
    };
    // 3. For each participant on the flow, create one instance of ParticipantUserModel
    const participant: UsersParticipantUserModel = {
      name: "Jack Bauer",
      email: "jack.bauer@mailinator.com",
      identifier: "75502846369",
    };
    // 4. Create a FlowActionCreateModel instance for each action (signature or approval) in the flow.
    //    This object is responsible for defining the personal data of the participant and the type of
    //    action that he will perform on the flow
    const flowAction: FlowActionsFlowActionCreateModel = {
      type: FlowActionType.Signer,
      user: participant,
    };

    // 5. Send the document create request
    const documentRequest: DocumentsCreateDocumentRequest = {
      files: [uploadModel],
      flowActions: [flowAction],
    };
    documentsApi.apiDocumentsPost(documentRequest).then((res) => {
      const actionUrlRequest: DocumentsActionUrlRequest ={
        identifier : participant.identifier
      }
      documentsApi.apiDocumentsIdActionUrlPost(res.data[0].documentId, actionUrlRequest).then((res)=>{

         console.log(res.data.embedUrl)
      })
  });
  });
