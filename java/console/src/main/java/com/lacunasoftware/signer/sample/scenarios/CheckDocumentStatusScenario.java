package com.lacunasoftware.signer.sample.scenarios;

import java.io.IOException;

import com.google.gson.Gson;
import com.lacunasoftware.signer.ActionStatus;
import com.lacunasoftware.signer.WebhookTypes;
import com.lacunasoftware.signer.documents.CreateDocumentResult;
import com.lacunasoftware.signer.documents.DocumentModel;

import com.lacunasoftware.signer.flowactions.FlowActionModel;

import com.lacunasoftware.signer.javaclient.exceptions.RestException;
import com.lacunasoftware.signer.webhooks.*;

public class CheckDocumentStatusScenario extends Scenario implements IWebhookHandlerScenario  {
    /**
     * This scenario demonstrates how to check if a document is concluded and the
     * status of it's flow actions.
     */
    @Override
    public void Run() throws IOException, RestException {
        CreateDocumentResult result = createDocument();

        // 1. Get the document's details by it's id
        DocumentModel details = signerClient.getDocumentDetails(result.getDocumentId());

        // 2. Check if the whole flow is concluded
        if (details.isIsConcluded()) {

        }

        // 3. If needed, check the status of individual flow actions
        for (FlowActionModel flowAction : details.getFlowActions()) {
            if (flowAction.getStatus() == ActionStatus.COMPLETED) {

            }
        }

        /**
         * NOTE:
         * 
         * The best way to know the exact time a document's flow is concluded is by
         * enabling a webhook in your organization on the application. Whenever the flow
         * of a document is completed, the application will fire a Webhook event by
         * sending a POST request to a registered URL.
         * 
         * You can find below an example of the handling logic of a webhook event.
         * 
         * Access the following link for information on available Webhook events:
         * https://dropsigner.com/swagger
         */
    }

    @Override
	public void HandleWebhook(WebhookModel webhook) {
        //Be aware when deserializing OffsetDateOfTime using another Json library, you must pay attention to it's data type
        //We have a specific method to deserialize OffsetDateOfTime fields using signerClient:
        Gson gson = signerClient.getGson();

        if (webhook != null) {
            // We parse webhook.data to json again to deserialize it properly
           String webhookData = gson.toJson(webhook.getData());
            if (webhook.getType() == WebhookTypes.DOCUMENTCONCLUDED) {
                DocumentConcludedModel documentConcludedModel = gson.fromJson(webhookData, DocumentConcludedModel.class);
                System.out.println(String.format("Document %s is concluded", documentConcludedModel.getId()));
            }
            if (webhook.getType() == WebhookTypes.DOCUMENTREFUSED) {
                DocumentRefusedModel documentRefusedModel = gson.fromJson(webhookData, DocumentRefusedModel.class);
                System.out.println(String.format("Document %s is Refused", documentRefusedModel.getId()));
            }
            if (webhook.getType() == WebhookTypes.DOCUMENTAPPROVED) {
                DocumentApprovedModel documentApprovedModel = gson.fromJson(webhookData, DocumentApprovedModel.class);
                System.out.println(String.format("Document %s is Approved", documentApprovedModel.getId()));
            }
            if (webhook.getType() == WebhookTypes.DOCUMENTSIGNED) {
                DocumentSignedModel documentSignedModel = gson.fromJson(webhookData, DocumentSignedModel.class);
                System.out.println(String.format("Document %s is Signed", documentSignedModel.getId()));
            }
        }
	}
}