package com.lacunasoftware.signer.sample.scenarios;

import java.io.IOException;
import java.util.ArrayList;

import com.lacunasoftware.signer.documents.CreateDocumentRequest;
import com.lacunasoftware.signer.documents.CreateDocumentResult;
import com.lacunasoftware.signer.FileUploadModel;
import com.lacunasoftware.signer.flowactions.FlowActionCreateModel;
import com.lacunasoftware.signer.FlowActionType;
import com.lacunasoftware.signer.users.ParticipantUserModel;
import com.lacunasoftware.signer.reserveds.RestException;
import com.lacunasoftware.signer.reserveds.UploadModel;
import com.lacunasoftware.signer.sample.Util;
import com.lacunasoftware.signer.reserveds.FileUploadModelBuilder;

public class CreateDocumentWithSigningRuleScenario extends Scenario {
    /**
    * This scenario demonstrates the creation of a document
    * that will be signed by a sign rule.
    * In a signing rule multiples users are assigned to the 
    * same action but just an arbitrary number of them are 
    * required to sign in order to complete that action.
    */
    @Override
    public void Run() throws IOException, RestException {
        // 1. The file's bytes must be read by the application and uploaded
        byte[] content = Util.getInstance().getResourceFile("sample.pdf");
		UploadModel uploadModel = signerClient.uploadFile("sample.pdf", content, "application/pdf");

        // 2. Define the name of the document which will be visible in the application
        FileUploadModelBuilder fileUploadModelBuilder = new FileUploadModelBuilder(uploadModel);
        fileUploadModelBuilder.setDisplayName("Signing Rule Sample");
        
        // 3. For each participant on the flow, create one instance of ParticipantUserModel
        ParticipantUserModel participantUserOne = new ParticipantUserModel();
		participantUserOne.setName("Jack Bauer");
		participantUserOne.setEmail("jack.bauer@mailinator.com");
        participantUserOne.setIdentifier("75502846369");
        
		ParticipantUserModel participantUserTwo = new ParticipantUserModel();
		participantUserTwo.setName("James Bond");
		participantUserTwo.setEmail("james.bond@mailinator.com");
        participantUserTwo.setIdentifier("95588148061");

        // 4. Each signing rule requires just one FlowActionCreateModel no matter
        //    the number of participants assigned to it. The participants are assigned to
        //    it via a list of ParticipantUserModel assigned to the `SignRuleUsers` property.
        //    The number of required signatures from this list of participants is represented by
        //    the property `NumberRequiredSignatures`.
        FlowActionCreateModel flowActionCreateModelSigningRule = new FlowActionCreateModel();
        flowActionCreateModelSigningRule.setType(FlowActionType.SIGNRULE);
        flowActionCreateModelSigningRule.setNumberRequiredSignatures(1);
        flowActionCreateModelSigningRule.setSignRuleUsers(new ArrayList<ParticipantUserModel>() {
            private static final long serialVersionUID = 1L;
            {
                add(participantUserOne);
                add(participantUserTwo);
            }
        });

        // 5. Send the document create request
        CreateDocumentRequest documentRequest = new CreateDocumentRequest();
        documentRequest.setFiles(new ArrayList<FileUploadModel>() {
            private static final long serialVersionUID = 1L;
            {
                add(fileUploadModelBuilder.toModel());
            }
        });
        documentRequest.setFlowActions(new ArrayList<FlowActionCreateModel>() {
            private static final long serialVersionUID = 1L;
            {
                add(flowActionCreateModelSigningRule);
            }
        });
        CreateDocumentResult result = signerClient.createDocument(documentRequest).get(0);
        
        System.out.println(String.format("Document %s created", result.getDocumentId().toString()));
    }
}