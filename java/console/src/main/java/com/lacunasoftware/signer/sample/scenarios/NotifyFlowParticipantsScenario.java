package com.lacunasoftware.signer.sample.scenarios;

import java.io.IOException;

import com.lacunasoftware.signer.CreateDocumentResult;
import com.lacunasoftware.signer.DocumentModel;
import com.lacunasoftware.signer.FlowActionModel;
import com.lacunasoftware.signer.RestException;

public class NotifyFlowParticipantsScenario extends Scenario {
    /**
    * This scenario demonstrates how to notify participants 
    * of the flow.
    */
    @Override
    public void Run() throws IOException, RestException, Exception {
        // 1. Get a document Id
        CreateDocumentResult result = createDocument();

        // 2. Get the document details
        DocumentModel details = signerClient.getDocumentDetails(result.getDocumentId());

        // 3. Notify each participant individually if necessary
        //    Note: Only participants with pending actions are notified.
        for (FlowActionModel flowAction : details.getFlowActions()) {
            signerClient.sendFlowActionReminder(result.getDocumentId(), flowAction.getId());
        }
    }
}