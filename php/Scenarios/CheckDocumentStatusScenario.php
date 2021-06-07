<?php


namespace Lacuna\Scenarios;


use Lacuna\Signer\Model\ActionStatus;
use Lacuna\Signer\Model\DocumentsDocumentModel;
use Lacuna\Signer\Model\FlowActionsFlowActionModel;
use Lacuna\Signer\Model\UsersParticipantUserModel;
use Lacuna\Signer\Model\WebhooksDocumentConcludedModel;
use Lacuna\Signer\Model\WebhooksWebhookModel;
use Lacuna\Signer\Model\WebhookTypes;

class CheckDocumentStatusScenario extends Scenario implements IWebhookHandlerScenario
{
    /**
     * This scenario demonstrates how to check if a document is concluded and the
     * status of it's flow actions.
     */
    function run()
    {
        $document = $this->createDocument();

        // 1. Get the document's details by it's id
        $documentDetails = new DocumentsDocumentModel($this->signerClient->getDocumentDetails($document->getDocumentId()));

        // 2. Check if the whole flow is concluded
        if ($documentDetails->getIsConcluded()) {
            echo "Document concluded";
        }

        // 3. If needed, check the status of individual flow actions
        foreach ($documentDetails->getFlowActions() as $item) {

            if ($item["status"] == ActionStatus::PENDING) {
                $user = new UsersParticipantUserModel($item["user"]);
                echo "The status of User " . $user->getName() . " is Pending\n";
            }
        }

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
    }

    function HandleWebhook($webhook)
    {
        $webhook = new WebhooksWebhookModel(json_decode($webhook, true));

        if ($webhook != null) {

            if ($webhook->getType() == WebhookTypes::DOCUMENT_CONCLUDED) {
                //3 - Now we are able to deserialize webhookData and use the related Document model type to store and use it's specifics
                $documentConcludedModel = new WebhooksDocumentConcludedModel($webhook->getData());
                echo "Document " . $documentConcludedModel->getId() . " is concluded";
            }
            if ($webhook->getType() == WebhookTypes::DOCUMENT_REFUSED) {
                $documentConcludedModel = new WebhooksDocumentConcludedModel($webhook->getData());
                echo "Document " . $documentConcludedModel->getId() . " is Refused";
            }
            if ($webhook->getType() == WebhookTypes::DOCUMENT_APPROVED) {
                $documentConcludedModel = new WebhooksDocumentConcludedModel($webhook->getData());
                echo "Document " . $documentConcludedModel->getId() . " is Approved";
            }
            if ($webhook->getType() == WebhookTypes::DOCUMENT_SIGNED) {
                $documentConcludedModel = new WebhooksDocumentConcludedModel($webhook->getData());
                echo "Document " . $documentConcludedModel->getId() . " is Signed";
            }
        }
    }
}