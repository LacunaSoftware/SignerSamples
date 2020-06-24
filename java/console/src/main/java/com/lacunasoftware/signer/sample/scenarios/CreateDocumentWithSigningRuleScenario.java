package com.lacunasoftware.signer.sample.scenarios;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import com.lacunasoftware.signer.ActionStatus;
import com.lacunasoftware.signer.CreateDocumentRequest;
import com.lacunasoftware.signer.CreateDocumentResult;
import com.lacunasoftware.signer.DocumentModel;
import com.lacunasoftware.signer.FileUploadModel;
import com.lacunasoftware.signer.FlowActionCreateModel;
import com.lacunasoftware.signer.FlowActionModel;
import com.lacunasoftware.signer.FlowActionType;
import com.lacunasoftware.signer.ParticipantUserModel;
import com.lacunasoftware.signer.RestException;
import com.lacunasoftware.signer.UploadModel;
import com.lacunasoftware.signer.sample.Util;

public class CreateDocumentWithSigningRuleScenario extends Scenario {
    /**
     * This scenario shows step-by-step the submission of a document
     * to the signer instance where there's a signing rule for it.
     * A signing rule is a modality where multiples participants are
     * assigned to the document but just an arbitrary number of them 
     * are needed to sign in order to complete the flow.
     */
    @Deprecated
    @Override
    public void Run() throws IOException, RestException {
        // 1. The file's bytes must be read by the application and uploaded using the method UploadFileAsync.
        byte[] content = Util.getInstance().getResourceFile("sample.pdf");
		UploadModel uploadModel = signerClient.uploadFile("sample.pdf", content, "application/pdf");

        // 2. Signer's server expects a FileUploadModel's list to create a document.
        FileUploadModel fileUploadModel = new FileUploadModel(uploadModel);
        fileUploadModel.setDisplayName("Signing Rule " + OffsetDateTime.now(ZoneOffset.UTC).toString());
        List<FileUploadModel> fileUploadModelList = new ArrayList<>();
		fileUploadModelList.add(fileUploadModel);

        // 3. Foreach participant on the flow, you'll need to create an instance of ParticipantUserModel.
        ParticipantUserModel participantUserOne = new ParticipantUserModel();
		participantUserOne.setName("Jack Bauer");
		participantUserOne.setEmail("jack.bauer@mailinator.com");
        participantUserOne.setIdentifier("75502846369");
        
		ParticipantUserModel participantUserTwo = new ParticipantUserModel();
		participantUserTwo.setName("James Bond");
		participantUserTwo.setEmail("james.bond@mailinator.com");
        participantUserTwo.setIdentifier("95588148061");

        // 4. You'll need to create a FlowActionCreateModel's instance foreach ParticipantUserModel
        //    created in the previous step. The FlowActionCreateModel is responsible for holding
        //    the personal data of the participant and the type of action that it will peform on the flow.
        //    In the case of order for the flow actions it's necessary to assign values to the `Step` propertie
        //    of the instances, smaller numbers represents action that comes first.
        FlowActionCreateModel flowActionCreateModelSigningRule = new FlowActionCreateModel();
        flowActionCreateModelSigningRule.setType(FlowActionType.SIGN_RULE);
        flowActionCreateModelSigningRule.setNumberRequiredSignatures(1);

        List<ParticipantUserModel> participantUserModels = new ArrayList<ParticipantUserModel>();
        participantUserModels.add(participantUserOne);
        participantUserModels.add(participantUserTwo);
        flowActionCreateModelSigningRule.setSignRuleUsers(participantUserModels);
        
        // 5. Signer's server expects a FlowActionCreateModel's list to create a document.
        List<FlowActionCreateModel> flowActionCreateModelList = new ArrayList<>();
        flowActionCreateModelList.add(flowActionCreateModelSigningRule);

        // 6. To create the document request, use the list of FileUploadModel and the list of FlowActionCreateModel.
        CreateDocumentRequest documentRequest = new CreateDocumentRequest();
        documentRequest.setFiles(fileUploadModelList);
        documentRequest.setFlowActions(flowActionCreateModelList);
        List<CreateDocumentResult> documentResults = signerClient.createDocument(documentRequest);

        // 7. To notify the participant:
        for (CreateDocumentResult documentResult : documentResults) {
            // 7.1. Extract the information of the flow using the following procedure.
            DocumentModel details = signerClient.getDocumentDetails(documentResult.getDocumentId());
            List<FlowActionModel> flowActions = details.getFlowActions();
            for (FlowActionModel flowAction : flowActions) {
                // 7.2. Send notification to the participant.
                if (flowAction.getStatus() == ActionStatus.PENDING) {
                    signerClient.sendFlowActionReminder(documentResult.getDocumentId(), flowAction.getId());
                }
            }
        }
    }
}