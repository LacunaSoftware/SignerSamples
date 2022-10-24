"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const signer_node_client_1 = require("signer-node-client");
/**
 * NOTE:
 *
 * The best way to know the exact time a document's flow is concluded, signed, approved or refused is by enabling a webhook in your organization on the
 * application. Whenever the flow of a document has one of these steps done, the application will fire a Webhook event by
 * sending a POST request to a registered URL.
 *
 * You can find below an example of the handling logic of a webhook event.
 *
 * Access the following link for information on available Webhook events:
 * https://dropsigner.com/swagger
 */
function handleWebhook(webhookModel) {
    const webhook = {
        data: webhookModel,
    };
    if (webhook != null) {
        if (webhook.type == signer_node_client_1.WebhookTypes.DocumentConcluded) {
            const documentConcludedModel = webhook.data;
            console.log(`Document ${documentConcludedModel.id} is concluded`);
        }
        if (webhook.type == signer_node_client_1.WebhookTypes.DocumentRefused) {
            const documentRefusedModel = webhook.data;
            console.log(`Document ${documentRefusedModel.id} is refused`);
        }
        if (webhook.type == signer_node_client_1.WebhookTypes.DocumentApproved) {
            const documentApprovedModel = webhook.data;
            console.log(`Document ${documentApprovedModel.id} is approved`);
        }
        if (webhook.type == signer_node_client_1.WebhookTypes.DocumentSigned) {
            const documentSignedModel = webhook.data;
            console.log(`Document ${documentSignedModel.id} is signed`);
        }
    }
}
