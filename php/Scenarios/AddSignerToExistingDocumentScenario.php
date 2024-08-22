<?php


namespace Lacuna\Scenarios;

use Lacuna\Signer\Model\DocumentsDocumentFlowEditRequest;
use Lacuna\Signer\Model\DocumentsDocumentModel;
use Lacuna\Signer\Model\FlowActionsFlowActionCreateModel;
use Lacuna\Signer\Model\FlowActionType;
use Lacuna\Signer\Model\UsersParticipantUserModel;

class AddSignerToExistingDocumentScenario extends Scenario
{

    /**
     * This scenario demonstrates how to add a new flowAction in a document flow.
     */
    function run()
    {
        $doc = $this->createDocument();

        // 1. Get the document details
        $details = new DocumentsDocumentModel($this->signerClient->getDocumentDetails($doc->getDocumentId()));

        // 2. Input the ongoing flowActionId to be able to change previously defined FlowActions
        $flowActionArray = $details->getFlowActions();

        // 3. For each participant on the new flow, create one instance of ParticipantUserModel
        $user = new UsersParticipantUserModel();
        $user->setName('Anakin Skywalker');
        $user->setEmail('anakin.skywalker@mailnator.com');
        $user->setIdentifier("75502846369");

        // 4. Create a FlowActionCreateModel instance for each action (signature or approval) in the flow.
        //    This object is responsible for defining the personal data of the participant and the type of
        //    action that he will perform on the flow
        $flowActionCreateModel = new FlowActionsFlowActionCreateModel();
        $flowActionCreateModel->setType(FlowActionType::SIGNER);
        $flowActionCreateModel->setUser($user);

        // 5. The new flow action step must be greater or equal to the current pending step
        $flowActionCreateModel->setStep(count($flowActionArray) + 1);

        // 6. Prepare the request
        $documentFlowEditRequest = new DocumentsDocumentFlowEditRequest();
        $documentFlowEditRequest->setAddedFlowActions(
            array($flowActionCreateModel)
        );
        
        // 7. Pass the parameters to the editflow function to perform the request
        $this->signerClient->editFlow($doc->getDocumentId(), $documentFlowEditRequest);

        // 8. Send a reminder to the new participants of the document flow
        $this->remindFlowUsersWithPendingActions($doc->getDocumentId());
    }

    /**
     * Sends a reminder to all users with pending actions in the document flow.
     *
     * This function retrieves the current flow actions associated with a document,
     * checks for any actions that have a status of 'Pending', and sends a reminder
     * to the corresponding participants to prompt them to complete their actions.
     *
     * @param string $documentId The ID of the document for which to send reminders.
     */
    public function remindFlowUsersWithPendingActions($documentId)
    {
        $details = new DocumentsDocumentModel($this->signerClient->getDocumentDetails($documentId));

        foreach ($details->getFlowActions() as $flowAction) {
            if ($flowAction['status'] == 'Pending') {
                $this->signerClient->sendFlowActionReminder($documentId, $flowAction['id']);
            }
        }
    }
}
