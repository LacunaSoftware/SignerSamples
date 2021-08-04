<?php


namespace Lacuna\Scenarios;


use Lacuna\Signer\Model\DocumentsCancelDocumentRequest;
use Lacuna\Signer\Model\RefusalRefusalRequest;

class CancelDocumentScenario extends Scenario
{
    /**
     * This scenario demonstrates how to cancel a document as an organization application.
     */
    function run()
    {
        //1 - You need a document id
        $document = $this->createDocument();
        $documentId = $document->getDocumentId();

        //2 - Create a cancellation request and give it a reason
        $cancelDocument  = new DocumentsCancelDocumentRequest();
        $cancelDocument->setReason("This is a document cancellation");

        //3 - Send the cancellation request
        $this->signerClient->cancelDocument($documentId, $cancelDocument);

    }
}