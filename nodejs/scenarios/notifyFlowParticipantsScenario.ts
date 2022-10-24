import {
  DocumentsApi,
  NotificationsApi,
} from "signer-node-client";
import { config, CreateDocument } from "./scenario";
/**
 * This scenario demonstrates how to notify participants
 * of the flow.
 */

// 1. Get a document Id
CreateDocument().then((response) => {
  const notification = new NotificationsApi(config);
  const document = new DocumentsApi(config);
  // 2. Get the document details
  document.apiDocumentsIdGet(response.documentId).then((res) => {
    res.data.flowActions.forEach((element) => {
      // 3. Notify each participant individually if necessary
      //    Note: Only participants with pending actions are notified.
      notification
        .apiNotificationsFlowActionReminderPost({
          flowActionId: element.id,
          documentId: response.documentId,
        })
        .then((resp) =>
          console.log("Participante Notified, Status :", resp.status)
        );
    });
  });
});
