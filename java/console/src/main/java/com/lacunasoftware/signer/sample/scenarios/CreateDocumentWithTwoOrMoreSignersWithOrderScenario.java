package com.lacunasoftware.signer.sample.scenarios;

import java.io.IOException;
import java.util.ArrayList;

import com.lacunasoftware.signer.CreateDocumentRequest;
import com.lacunasoftware.signer.CreateDocumentResult;
import com.lacunasoftware.signer.FileUploadModel;
import com.lacunasoftware.signer.FlowActionCreateModel;
import com.lacunasoftware.signer.FlowActionType;
import com.lacunasoftware.signer.ParticipantUserModel;
import com.lacunasoftware.signer.RestException;
import com.lacunasoftware.signer.UploadModel;
import com.lacunasoftware.signer.sample.Util;

public class CreateDocumentWithTwoOrMoreSignersWithOrderScenario extends Scenario{
    /**
    * This scenario demonstrates the creation of a document with 
    * two signers and there's a required order for the signatures.
    */
    @Override
    public void Run() throws IOException, RestException {
        // 1. The file's bytes must be read by the application and uploaded
        byte[] content = Util.getInstance().getResourceFile("sample.pdf");
		UploadModel uploadModel = signerClient.uploadFile("sample.pdf", content, "application/pdf");

        // 2. Define the name of the document which will be visible in the application
        FileUploadModel fileUploadModel = new FileUploadModel(uploadModel);
        fileUploadModel.setDisplayName("Two Signers With Order");

        // 3. For each participant on the flow, create one instance of ParticipantUserModel
        ParticipantUserModel participantUserOne = new ParticipantUserModel();
		participantUserOne.setName("Jack Bauer");
		participantUserOne.setEmail("jack.bauer@mailinator.com");
        participantUserOne.setIdentifier("75502846369");
        
		ParticipantUserModel participantUserTwo = new ParticipantUserModel();
		participantUserTwo.setName("James Bond");
		participantUserTwo.setEmail("james.bond@mailinator.com");
        participantUserTwo.setIdentifier("95588148061");

        // 4. Create a FlowActionCreateModel instance for each action (signature or approval) in the flow.
        //    This object is responsible for defining the personal data of the participant, the type of 
        //    action that he will peform on the flow and the order in which this action will take place
        //    (Step property)
        FlowActionCreateModel flowActionCreateModelOne = new FlowActionCreateModel();
        flowActionCreateModelOne.setType(FlowActionType.SIGNER);
        flowActionCreateModelOne.setUser(participantUserOne);
        flowActionCreateModelOne.setStep(1);

        FlowActionCreateModel flowActionCreateModelTwo = new FlowActionCreateModel();
        flowActionCreateModelTwo.setType(FlowActionType.SIGNER);
        flowActionCreateModelTwo.setUser(participantUserTwo);
        flowActionCreateModelTwo.setStep(2);

        // 5. Send the document create request
        CreateDocumentRequest documentRequest = new CreateDocumentRequest();
        documentRequest.setFiles(new ArrayList<FileUploadModel>() {
            private static final long serialVersionUID = 1L;
            {
                add(fileUploadModel);
            }
        });
        documentRequest.setFlowActions(new ArrayList<FlowActionCreateModel>() {
            private static final long serialVersionUID = 1L;
            {
                add(flowActionCreateModelOne);
                add(flowActionCreateModelTwo);
            }
        });
        CreateDocumentResult result = signerClient.createDocument(documentRequest).get(0);
        
        System.out.println(String.format("Document %s created", result.getDocumentId().toString()));
    }
}