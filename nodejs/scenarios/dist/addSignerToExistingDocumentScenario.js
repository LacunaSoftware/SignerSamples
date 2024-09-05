"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
Object.defineProperty(exports, "__esModule", { value: true });
const signer_node_client_1 = require("signer-node-client");
const scenario_1 = require("./scenario");
/**
 * This scenario demonstrates the creation of a document into an existing folder.
 */
const documentsApi = new signer_node_client_1.DocumentsApi(scenario_1.config);
const notificationsApi = new signer_node_client_1.NotificationsApi(scenario_1.config);
const fileName = "sample.pdf";
// 1. The file's bytes must be read by the application and uploaded
(0, scenario_1.CreateDocument)().then((res) => __awaiter(void 0, void 0, void 0, function* () {
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
    let flowAction = {
        type: signer_node_client_1.FlowActionType.Signer,
        user: participant,
    };
    const files = [fileUploadModel];
    let flowActions = [flowAction];
    // 5. The new flow action step must be greater or equal to the current pending step
    flowAction.step = flowActions.length + 1;
    // 6. Prepare the request
    let documentFlowEditRequest = {
        addedFlowActions: flowActions,
    };
    // 7. Pass the parameters to the editflow function to perform the request
    documentsApi.apiDocumentsIdFlowPost(docId, documentFlowEditRequest).then(() => __awaiter(void 0, void 0, void 0, function* () {
        // 8. Send a reminder to the new participants of the document flow
        yield remindFlowUsersWithPendingActions(docId);
    }))
        .catch((ex) => {
        console.error(ex);
    });
}));
/**
 * Sends a reminder to all users with pending actions in the document flow.
 *
 * This function retrieves the current flow actions associated with a document,
 * checks for any actions that have a status of 'Pending', and sends a reminder
 * to the corresponding participants to prompt them to complete their actions.
 *
 * @param string documentId The ID of the document for which to send reminders.
 */
function remindFlowUsersWithPendingActions(documentId) {
    return __awaiter(this, void 0, void 0, function* () {
        documentsApi.apiDocumentsIdGet(documentId).then((res) => {
            let details = res.data; // type assertion
            const flowActions = details.flowActions;
            flowActions.forEach((flowAction) => {
                if (flowAction.status == signer_node_client_1.ActionStatus.Pending) {
                    let notificationsCreateFlowActionReminderRequest = {
                        documentId: documentId,
                        flowActionId: flowAction.id,
                    };
                    notificationsApi.apiNotificationsFlowActionReminderPost(notificationsCreateFlowActionReminderRequest).then((res) => {
                        console.log(res.status);
                    });
                }
            });
        });
    });
}
