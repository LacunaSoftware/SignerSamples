import {
    UploadApi,
    FlowActionType,
    FoldersApi,
    DocumentsApi,
    FlowActionsFlowActionCreateModel,
    getBase64,
    DocumentsDocumentFlowEditRequest,
    DocumentsDocumentModel,
    ActionStatus,
    NotificationsApi,
    NotificationsCreateFlowActionReminderRequest,
    FlowActionsFlowActionModel,
} from "signer-node-client";
import { CreateDocument, config } from "./scenario";

/**
 * This scenario demonstrates the creation of a document into an existing folder.
 */
const documentsApi = new DocumentsApi(config);
const notificationsApi = new NotificationsApi(config);
const fileName = "sample.pdf";


// 1. The file's bytes must be read by the application and uploaded
CreateDocument().then(async(res) => {
    //1. You need a document id
        const docId = res.documentId;
        // 2. Define the name of the document which will be visible in the application
        const fileUploadModel = {
            displayName: "Add signer to existing document scenario sample",
            id: docId,
            contentType: "application/pdf",
            name: fileName,
        };
        // 3. For each participant on the flow, create one instance of ParticipantUserModel
        const participant = {
            name: "Anakin Skywalker",
            email: "anakin.skywalker@mailnator.com",
            identifier: "75502846369",
        };

        // 4. Create a FlowActionCreateModel instance for each action (signature or approval) in the flow.
        //    This object is responsible for defining the personal data of the participant and the type of
        //    action that he will perform on the flow
        let flowAction: FlowActionsFlowActionCreateModel = {
            type: FlowActionType.Signer,
            user: participant,
        };
        const files = [fileUploadModel];
        let flowActions: FlowActionsFlowActionCreateModel[] = [flowAction];

        // 5. The new flow action step must be greater or equal to the current pending step
        flowAction.step = flowActions.length + 1;

        // 6. Prepare the request
        let documentFlowEditRequest: DocumentsDocumentFlowEditRequest = {
            addedFlowActions: flowActions,
        };

        // 7. Pass the parameters to the editflow function to perform the request
        documentsApi.apiDocumentsIdFlowPost(
            docId,
            documentFlowEditRequest
        ).then(async () => {
            // 8. Send a reminder to the new participants of the document flow
            await remindFlowUsersWithPendingActions(docId);
        })
        .catch((ex) => {
            console.error(ex);
        })

    });
/**
 * Sends a reminder to all users with pending actions in the document flow.
 *
 * This function retrieves the current flow actions associated with a document,
 * checks for any actions that have a status of 'Pending', and sends a reminder
 * to the corresponding participants to prompt them to complete their actions.
 *
 * @param string documentId The ID of the document for which to send reminders.
 */
async function remindFlowUsersWithPendingActions(documentId: string) {
    documentsApi.apiDocumentsIdGet(documentId).then((res: DocumentsDocumentModel|any) => {
        let details = res.data as DocumentsDocumentModel; // type assertion
        const flowActions = details.flowActions;

        flowActions.forEach((flowAction: FlowActionsFlowActionModel) => {
            if (flowAction.status == ActionStatus.Pending) {
                let notificationsCreateFlowActionReminderRequest: NotificationsCreateFlowActionReminderRequest =
                    {
                        documentId: documentId,
                        flowActionId: flowAction.id,
                    };
    
                notificationsApi.apiNotificationsFlowActionReminderPost(
                    notificationsCreateFlowActionReminderRequest
                ).then((res) => {
                    console.log(res.status);
                });
            }
        });
    });
}
