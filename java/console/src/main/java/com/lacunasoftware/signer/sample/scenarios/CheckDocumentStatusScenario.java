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
        //Be aware when deserializing OffsetDateOfTime using another Json library, you must pay attention to it's data type setup
        //We have two specific methods, getGson() and getJackson(), to access a instance of Gson or Jackson.
        //They are already took care of the proper configuration to deserialize OffsetDateOfTime fields and to use them you must
        // follow the steps bellow:

        //1 - After passing the webhook model to your method (in this case, "webhook" was deserialized before calling
        // "HandleWebhook" method) you need to get a instance of your selected Json library
        Gson gson = signerClient.getGson();

        if (webhook != null) {

            // 2 - We parse webhook.data to json again to deserialize it properly
           String webhookData = gson.toJson(webhook.getData());

            if (webhook.getType() == WebhookTypes.DOCUMENTCONCLUDED) {
                //3 - Now we are able to deserialize webhookData and use the related Document model type to store and use it's specifics
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
        /**
         * NOTE:
         * If you want to setup your own Json deserializer/serializer instance take a look inside our library,
         * more specifically, inside RestClient.class to see which are the pre-sets that you need to use in your serializer.
         *
         */

	}
}