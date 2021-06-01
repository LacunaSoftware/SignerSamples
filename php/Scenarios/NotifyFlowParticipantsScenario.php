<?php


namespace Lacuna\Scenarios;


use Lacuna\Signer\Model\DocumentsDocumentModel;
use Lacuna\Signer\Model\FlowActionsFlowActionModel;

class NotifyFlowParticipantsScenario extends Scenario
{
    /**
     * This scenario demonstrates how to notify participants
     * of the flow.
     */
    function run()
    {
        // 1. Get a document Id
        $result = $this->createDocument();

        // 2. Get the document details
        $details = new DocumentsDocumentModel($this->signerClient->getDocumentDetails($result->getDocumentId()));

        // 3. Notify each participant individually if necessary
        //    Note: Only participants with pending actions are notified.
        foreach ($details->getFlowActions() as $item) {
            $flowAction = new FlowActionsFlowActionModel($item);
            var_dump($flowAction);
            $this->signerClient->sendFlowActionReminder($result->getDocumentId(), $flowAction->getId());
        }

    }

}