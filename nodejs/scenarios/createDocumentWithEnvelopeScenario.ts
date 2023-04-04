import {
  UploadApi,
  DocumentsApi,
  FileUploadModel,
  UsersParticipantUserModel,
  FlowActionsFlowActionCreateModel,
  FlowActionType,
  getBase64,
} from "signer-node-client";
import { config } from "./scenario";

/**
 * This scenario demonstrates the creation of a document with description.
 */

const filePath = "../../samples/sample.pdf";
const fileName = "sample.pdf";

const uploadApi = new UploadApi(config);
const documentsApi = new DocumentsApi(config);

// 1. The file's bytes must be read by the application and uploaded
uploadApi
  .apiUploadsBytesPost({
    bytes: getBase64(filePath),
  })
  .then((responseOne) => {
    uploadApi
      .apiUploadsBytesPost({
        bytes: getBase64(filePath),
      })
      .then((responseTwo) => {
        // 2. Define the name of the document which will be visible in the application
        const fileUploadModelOne: FileUploadModel = {
          displayName: "Document sample",
          id: responseOne.data.id,
          contentType: "application/pdf",
          name: fileName,
        };

        const fileUploadModelTwo: FileUploadModel = {
          displayName: "Document sample",
          id: responseTwo.data.id,
          contentType: "application/pdf",
          name: fileName,
        };

        // 3. For each participant on the flow, create one instance of ParticipantUserModel
        const participant: UsersParticipantUserModel = {
          name: "Jack Bauer",
          email: "jack.bauer@mailnator.com",
          identifier: "75502846369",
        };

        // 4. Create a FlowActionCreateModel instance for each action (signature or approval) in the flow
        const flowAction: FlowActionsFlowActionCreateModel = {
          type: FlowActionType.Signer,
          user: participant,
        };

        // 5. Send the document create request
        documentsApi
          .apiDocumentsPost({
            files: [fileUploadModelOne, fileUploadModelTwo],
            // 6. Set up the attribute "isEnvelope" as "true" and you MUST give a name to this envelope "envelopeName"
            isEnvelope: true,
            envelopeName: "Name",
            flowActions: [flowAction],
          })
          .then((res) => {
            // 7. Print result
            console.log("Document ", res.data[0].documentId, "created");
          });
      });
  });
