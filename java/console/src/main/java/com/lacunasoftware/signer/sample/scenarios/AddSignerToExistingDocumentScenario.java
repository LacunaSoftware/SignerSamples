package com.lacunasoftware.signer.sample.scenarios;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.lacunasoftware.signer.ActionStatus;
import com.lacunasoftware.signer.FlowActionType;
import com.lacunasoftware.signer.documents.CreateDocumentResult;
import com.lacunasoftware.signer.documents.DocumentFlowEditRequest;
import com.lacunasoftware.signer.documents.DocumentModel;
import com.lacunasoftware.signer.flowactions.FlowActionCreateModel;
import com.lacunasoftware.signer.flowactions.FlowActionModel;
import com.lacunasoftware.signer.javaclient.exceptions.RestException;
import com.lacunasoftware.signer.users.ParticipantUserModel;

public class AddSignerToExistingDocumentScenario extends Scenario {

    @Override
    public void Run() throws IOException, RestException, Exception {
        // 0. Create the Document
        CreateDocumentResult document = createDocument();

        // 1. Get the document details
        DocumentModel details = signerClient.getDocumentDetails(document.getDocumentId());

        // 2. Input the ongoing flowActionId to be able to change previously defined
        // FlowActions
        List<FlowActionModel> flowActionArray = details.getFlowActions();

        // 3. For each participant on the new flow, create one instance of
        // ParticipantUserModel
        ParticipantUserModel user = new ParticipantUserModel();
        user.setName("Anakin Skywalker");
        user.setEmail("anakin.skywalker@mailnator.com");
        user.setIdentifier("75502846369");

        // 4. Create a FlowActionCreateModel instance for each action (signature or approval) in the flow.
        // This object is responsible for defining the personal data of the participant
        // and the type of action that he will perform on the flow
        FlowActionCreateModel flowActionCreateModel = new FlowActionCreateModel();
        flowActionCreateModel.setType(FlowActionType.SIGNER);
        flowActionCreateModel.setUser(user);

        // 5. The new flow action step must be greater or equal to the current pending step
        flowActionCreateModel.setStep(flowActionArray.size() + 1);

        // 6. Prepare the request
        DocumentFlowEditRequest documentFlowEditRequest = new DocumentFlowEditRequest();
        List<FlowActionCreateModel> flowActionCreateModelList = new ArrayList<FlowActionCreateModel>();
        flowActionCreateModelList.add(flowActionCreateModel);
        documentFlowEditRequest.setAddedFlowActions(flowActionCreateModelList);

        // 7. Pass the parameters to the editflow function to perform the request
        signerClient.updateDocumentFlow(document.getDocumentId(), documentFlowEditRequest);

        // 8. Send a reminder to the new participants of the document flow
        remindFlowUsersWithPendingFlowActions(document.getDocumentId());
    }

    /**
     * Sends a reminder to all users with pending actions in the document flow.
     *
     * This function retrieves the current flow actions associated with a document,
     * checks for any actions that have a status of 'Pending', and sends a reminder
     * to the corresponding participants to prompt them to complete their actions.
     *
     * @param string documentId The ID of the document for which to send reminders.
     */
    private void remindFlowUsersWithPendingFlowActions(UUID documentId) throws RestException {
        DocumentModel details = signerClient.getDocumentDetails(documentId);
        for (FlowActionModel flowAction : details.getFlowActions()) {
            if(flowAction.getStatus() == ActionStatus.PENDING){
                signerClient.sendFlowActionReminder(documentId, flowAction.getId());
            }
        }
    }

}


