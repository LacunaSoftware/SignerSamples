<?php


namespace Lacuna\Scenarios;


use Lacuna\Signer\Model\RefusalRefusalRequest;

class RefuseDocumentScenario extends Scenario
{
    /**
     * This scenario demonstrates how to refuse a document as an organization application.
     */
    function run()
    {
        //1. You need a document id
        $document = $this->createDocument();
        $documentId = $document->getDocumentId();

        //2 - Create a refusal request and give it a reason
        $refuseDocument = new RefusalRefusalRequest();
        $refuseDocument->setReason("This is a document refusal");

        //3 - Send the refusal request
        $this->signerClient->refuseDocument($documentId, $refuseDocument);

    }
}