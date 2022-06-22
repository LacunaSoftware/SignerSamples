<?php


namespace Lacuna\Scenarios;

use Lacuna\Signer\Model\DocumentsDocumentFlowEditRequest;
use Lacuna\Signer\Model\FlowActionsFlowActionEditModel;
use Lacuna\Signer\Model\DocumentsDocumentModel;
use Lacuna\Signer\Model\FlowActionsFlowActionModel;

class DocumentFlowEditRequestScenario extends Scenario
{
    /**
     * This scenario demonstrates how to edit a ongoing flowAction email.
     */
    function run()
    {
        $doc = $this->createDocument();

        $docId = $doc->getDocumentId();

        // 2. Get the document details
        $details = new DocumentsDocumentModel($this->signerClient->getDocumentDetails($doc->getDocumentId()));
    
        // 1. Create FlowActionEditModel
        $documentEdit = new FlowActionsFlowActionEditModel();
        // 2. Input the ongoing flowActionId to be able to change parameters
        foreach($details->getFlowActions() as $item){
            $flowAction = new FlowActionsFlowActionModel($item);
            $documentEdit->setFlowActionId($flowAction->getId());
        };
        // 3. Line below changes email address of participants in the current flow action (set by flowActionId)
        $documentEdit->setParticipantEmailAddress("michael.douglas@mailinator.com");

        // 4. (OPTIONAL): There are other rules that can be changed inside the FlowActionEditModel, uncomment and
        // set them as you wish
        // $documentEdit->setRuleName();
        // $documentEdit->setSignRuleUsers();
        // $documentEdit->setTitle();
        // $documentEdit->setPrePositionedMarks();

        // 5. Prepare the request
        $documentFlowEditRequest = new DocumentsDocumentFlowEditRequest();

        $documentFlowEditRequest->setEditedFlowActions(
            array($documentEdit)
        );
        // 6. (OPTIONAL) There are additional options to set inside the DocumentsDocumentFlowEditRequest object
        // $documentFlowEditRequest->setAddedFlowActions();
        // $documentFlowEditRequest->setDeletedFlowActionIds();
        // $documentFlowEditRequest->setAddedObservers();
        // $documentFlowEditRequest->setEditedObservers();
        // $documentFlowEditRequest->setDeletedObserverIds();

        return $this->signerClient->editFlow($docId, $documentFlowEditRequest);
    }
}
