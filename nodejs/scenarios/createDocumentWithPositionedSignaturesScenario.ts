import {
  UploadApi,
  DocumentsApi,
  FileUploadModel,
  UsersParticipantUserModel,
  FlowActionsFlowActionCreateModel,
  FlowActionType,
  DocumentMarkPrePositionedDocumentMarkModel,
  DocumentMarkType,
  getBase64
} from "signer-node-client";
import { config,  } from "./scenario";

const uploadApi = new UploadApi(config);
const documentsApi = new DocumentsApi(config);
const filepath = "../samples/sample.pdf";
const filename = "sample.pdf";

// 1. The file's bytes must be read by the application and uploaded
uploadApi
  .apiUploadsBytesPost({
    bytes: getBase64(filepath),
  })
  .then((res) => {
    // 2. Define the name of the document which will be visible in the application
    const fileUploadModel: FileUploadModel = {
      displayName: "Prepositioned signatures",
      id: res.data.id,
      contentType: "application/pdf",
      name: filename,
    };

    // 3. For each participant on the flow, create one instance of ParticipantUserModel
    const participant: UsersParticipantUserModel = {
      name: "Jack Bauer",
      email: "jack.bauer@mailnator.com",
      identifier: "75502846369",
    };

    // 4. Create a FlowActionCreateModel instance for each action (signature or approval) in the flow.
    //    This object is responsible for defining the personal data of the participant, the type of
    //    action that he will perform on the flow and the order in which this action will take place
    //    (Step property) and the pre-positioned marks for placing signatures. If the Step property of all action are the same or not specified they
    //    may be executed at any time
    const flowAction: FlowActionsFlowActionCreateModel = {
      type: FlowActionType.Signer,
      user: participant,
    };
    // 5.  Create the mark atributes
    const prepositionedDocumentMarkModel: DocumentMarkPrePositionedDocumentMarkModel =
      {
        type: DocumentMarkType.SignatureVisualRepresentation,
        uploadId: fileUploadModel.id,
        topLeftX: 395.0,
        topLeftY: 560.0,
        width: 170.0,
        height: 94.0,
        pageNumber: 1,
      };

    //Adding the mark attributes to the list (you can preposition marks on different documents)
    const listMark = [prepositionedDocumentMarkModel];

    flowAction.prePositionedMarks = listMark;

    // 6. Send the document create request
    documentsApi
      .apiDocumentsPost({
        files: [fileUploadModel],
        flowActions: [flowAction],
      })
      .then((res) => {
        console.log("Document ", res.data[0].documentId, "created");
      });
  });
