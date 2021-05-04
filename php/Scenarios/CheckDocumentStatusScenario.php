<?php


namespace Lacuna\Scenarios;


use Lacuna\Signer\Model\ActionStatus;
use Lacuna\Signer\Model\DocumentsDocumentModel;
use Lacuna\Signer\Model\FlowActionsFlowActionModel;
use Lacuna\Signer\Model\UsersParticipantUserModel;

class CheckDocumentStatusScenario extends Scenario
{
    /**
     * This scenario demonstrates how to check if a document is concluded and the
     * status of it's flow actions.
     */
    function run(){
        $document =  $this->createDocument();

        // 1. Get the document's details by it's id
        $documentDetails = new DocumentsDocumentModel($this->signerClient->getDocumentDetails($document->getDocumentId()));

        // 2. Check if the whole flow is concluded
        if ($documentDetails->getIsConcluded()) {
            echo "Document concluded";
        }


        // 3. If needed, check the status of individual flow actions
        foreach  ( $documentDetails->getFlowActions() as $item ) {

           if($item["status"]  == ActionStatus::PENDING){
               $user = new UsersParticipantUserModel($item["user"] );
               echo "The status of User " . $user->getName() . " is Pending\n";
           }
        }

    }

}