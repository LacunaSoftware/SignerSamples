<?php


namespace Lacuna\Scenarios;


use Lacuna\Signer\Model\RefusalRefusalRequest;

class RefuseDocumentScenario extends Scenario
{
    /**
     * This scenario demonstrates how to delete a document using it's ID.
     */
    function run()
    {
        //1. You need a document id
        $document = $this->createDocument();
        $documentId = $document->getDocumentId();

        //2. Call the api method to delete the document and pass the document Id as parameter
        $refuseDocument = new RefusalRefusalRequest();
        $refuseDocument->setReason("This is a document refusal");

        //3. If you want to very the document status use the method bellow
         $this->signerClient->refuseDocument($documentId, $refuseDocument);

         echo "teste";
    }

}