"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const signer_node_client_1 = require("signer-node-client");
const scenario_1 = require("./scenario");
/**
 * This scenario demonstrates how to notify participants
 * of the flow.
 */
// 1. Get a document Id
(0, scenario_1.CreateDocument)().then((response) => {
    const notification = new signer_node_client_1.NotificationsApi(scenario_1.config);
    const document = new signer_node_client_1.DocumentsApi(scenario_1.config);
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
                .then((resp) => console.log("Participante Notified, Status :", resp.status));
        });
    });
});
