<?php


namespace Lacuna\Scenarios;


use Lacuna\Signer\Model\DocumentsCreateDocumentResult;

class DeleteDocumentScenario extends Scenario
{
    function run (){
        $document =  $this->createDocument();
        $documentId = $document ->getDocumentId();

        $this->signerClient->deleteDocument($documentId);

        echo $this->signerClient->getDocumentDetails($documentId )['message'];
    }

}