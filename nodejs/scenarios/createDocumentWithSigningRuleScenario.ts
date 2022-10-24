import {
  DocumentsApi,
  FileUploadModel,
  UploadApi,
  UsersParticipantUserModel,
  FlowActionsFlowActionCreateModel,
  FlowActionType,
  DocumentsCreateDocumentRequest,
  getBase64
} from "signer-node-client";
import { config  } from "./scenario";

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
    displayName: "Signing Rule Sample",
  };
  // 3. For each participant on the flow, create one instance of ParticipantUserModel
  const participantUserOne: UsersParticipantUserModel = {
    name: "Jack Bauer",
    email: "jack.bauer@mailinator.com",
    identifier: "75502846369",
  };
  const participantUserTwo: UsersParticipantUserModel = {
    name: "James Bond",
    email: "james.bond@mailinator.com",
    identifier: "95588148061",
  };
  // 4. Each signing rule requires just one FlowActionCreateModel no matter
  //    the number of participants assigned to it. The participants are assigned to
  //    it via a list of ParticipantUserModel assigned to the `SignRuleUsers` property.
  //    The number of required signatures from this list of participants is represented by
  //    the property `NumberRequiredSignatures`.
  const flowAction: FlowActionsFlowActionCreateModel = {
    type: FlowActionType.SignRule,
    numberRequiredSignatures: 1,
    signRuleUsers: [participantUserOne, participantUserTwo],
  };

  // 5. Send the document create request
  const documentRequest: DocumentsCreateDocumentRequest = {
    files: [uploadModel],
    flowActions: [flowAction],
  };

  documentsApi.apiDocumentsPost(documentRequest).then((res) => {
    console.log("Document ", res.data[0].documentId, " Created");
  });
});
