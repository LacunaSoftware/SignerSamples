<?php


namespace Lacuna\Scenarios;


use Lacuna\Signer\Model\DocumentsDocumentFlowEditRequest;
use Lacuna\Signer\Model\FlowActionsFlowActionEditModel;
use Lacuna\Signer\Model\DocumentsDocumentModel;
use Lacuna\Signer\Model\FlowActionsFlowActionModel;
use Lacuna\Signer\Model\FlowActionsDocumentFlowEditResponse;
use Lacuna\Signer\Model\FlowActionType;

class DocumentFlowEditRequestScenario extends Scenario
{
    /**
     * This scenario demonstrates how to edit a ongoing flowAction email.
     */
    function run()
    {
        $doc = $this->createDocument();

        $docId = $doc->getDocumentId();

        // 1. Get the document details
        $details = new DocumentsDocumentModel($this->signerClient->getDocumentDetails($doc->getDocumentId()));
    
        // 2. Create FlowActionEditModel
        $documentEdit = new FlowActionsFlowActionEditModel();
        // 3. Input the ongoing flowActionId to be able to change previously defined FlowActions
        $flowActionArray = $details->getFlowActions(); 
        $flowAction = new FlowActionsFlowActionModel($flowActionArray[0]);
        $documentEdit->setFlowActionId($flowAction->getId());
        // 4. Line below changes email address of participants in the current flow action (set by flowActionId)
        $documentEdit->setParticipantEmailAddress("michael.douglas@mailinator.com");
        $documentEdit->setStep(1);

        // 5. (OPTIONAL): There are other rules that can be changed inside the FlowActionEditModel, uncomment and
        // set them as you wish
        // $documentEdit->setRuleName();
        // $documentEdit->setSignRuleUsers();
        // $documentEdit->setTitle();
        // $documentEdit->setPrePositionedMarks();

        // 6. Prepare the request
        $documentFlowEditRequest = new DocumentsDocumentFlowEditRequest();

        $documentFlowEditRequest->setEditedFlowActions(
            array($documentEdit)
        );
        // 7. (OPTIONAL) There are additional options to set inside the DocumentsDocumentFlowEditRequest object
        // $documentFlowEditRequest->setAddedFlowActions();
        // $documentFlowEditRequest->setDeletedFlowActionIds();
        // $documentFlowEditRequest->setAddedObservers();
        // $documentFlowEditRequest->setEditedObservers();
        // $documentFlowEditRequest->setDeletedObserverIds();

        $result = new FlowActionsDocumentFlowEditResponse();
        $result->setRectifiedParticipants($this->signerClient->editFlow($docId, $documentFlowEditRequest));

        //echo $result->getRectifiedParticipants();
        //print_r();
      
        echo json_encode($result->getRectifiedParticipants());
    }
}
