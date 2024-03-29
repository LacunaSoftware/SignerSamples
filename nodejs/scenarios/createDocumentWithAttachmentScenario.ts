import {
  UploadApi,
  DocumentsApi,
  FileUploadModel,
  UsersParticipantUserModel,
  FlowActionsFlowActionCreateModel,
  FlowActionType,
  getBase64,
  AttachmentsAttachmentUploadModel,
} from "signer-node-client";
import { config } from "./scenario";

/**
 * This scenario demonstrates the creation of a document with Attachment.
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
  .then((res) => {
    // 2. Define the name of the document which will be visible in the application
    const fileUploadModel: FileUploadModel = {
      displayName: "Document sample",
      id: res.data.id,
      contentType: "application/pdf",
      name: fileName,
    };

    // 3. Repeat the same steps above but now for the attachment and using AttachmentUploadBuilder
    uploadApi
      .apiUploadsBytesPost({
        bytes: getBase64(filePath),
      })
      .then((res) => {
        const attachmentFileUploadModelBuilder: AttachmentsAttachmentUploadModel =
          {
            id: res.data.id,
            name: fileName,
            contentType: "application/pdf",
            displayName: "One Attachment Sample",
            //here you can define if the attachment will be restrict to the organization participants (true) or to everyone in the document flow (false)
            isPrivate: false,
          };

        // 4. For each participant on the flow, create one instance of ParticipantUserModel
        const participant: UsersParticipantUserModel = {
          name: "Jack Bauer",
          email: "jack.bauer@mailnator.com",
          identifier: "75502846369",
        };

        // 5. Create a FlowActionCreateModel instance for each action
        const flowAction: FlowActionsFlowActionCreateModel = {
          type: FlowActionType.Signer,
          user: participant,
        };

        // 6. Send the document create request with attachment's attribute
        documentsApi
          .apiDocumentsPost({
            files: [fileUploadModel],
            // set the attachment
            attachments: [attachmentFileUploadModelBuilder],
            flowActions: [flowAction],
          })
          .then((res) => {
            // 7. Print result
            console.log("Document ", res.data[0].documentId, "created");
          });
      });
  });
