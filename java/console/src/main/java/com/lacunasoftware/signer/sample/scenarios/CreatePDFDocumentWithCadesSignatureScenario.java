package com.lacunasoftware.signer.sample.scenarios;

import java.io.IOException;
import java.util.ArrayList;

import com.lacunasoftware.signer.javaclient.models.UploadModel;
import com.lacunasoftware.signer.FileUploadModel;
import com.lacunasoftware.signer.FlowActionType;
import com.lacunasoftware.signer.documents.CreateDocumentRequest;
import com.lacunasoftware.signer.documents.CreateDocumentResult;
import com.lacunasoftware.signer.flowactions.FlowActionCreateModel;
import com.lacunasoftware.signer.javaclient.builders.FileUploadModelBuilder;
import com.lacunasoftware.signer.sample.Util;
import com.lacunasoftware.signer.users.ParticipantUserModel;
import com.lacunasoftware.signer.javaclient.exceptions.RestException;

public class CreatePDFDocumentWithCadesSignatureScenario extends Scenario {
    /**
    * This scenario demonstrates the creation of a PDF document
    * that needs to be signed using the CAdES format.
    */
    @Override
    public void Run() throws IOException, RestException {
        // 1. The file's bytes must be read by the application and uploaded
        byte[] content = Util.getInstance().getResourceFile("sample.pdf");
		UploadModel uploadModel = signerClient.uploadFile("sample.pdf", content, "application/pdf");

        // 2. Define the name of the document which will be visible in the application
        FileUploadModelBuilder fileUploadModelBuilder = new FileUploadModelBuilder(uploadModel);
        fileUploadModelBuilder.setDisplayName("One Signer Sample");

        // 3. For each participant on the flow, create one instance of ParticipantUserModel
        ParticipantUserModel user = new ParticipantUserModel();
		user.setName("Jack Bauer");
		user.setEmail("jack.bauer@mailinator.com");
        user.setIdentifier("75502846369");
        
        // 4. Create a FlowActionCreateModel instance for each action (signature or approval) in the flow.
        //    This object is responsible for defining the personal data of the participant and the type of 
        //    action that he will peform on the flow
        FlowActionCreateModel flowActionCreateModel = new FlowActionCreateModel();
        flowActionCreateModel.setType(FlowActionType.SIGNER);
        flowActionCreateModel.setUser(user);

        // 5. Send the document create request specifying that it requires CAdES signatures, since PAdES is
        //    the default for PDF files.
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
                add(flowActionCreateModel);
            }
        });
        documentRequest.setForceCadesSignature(true);
        CreateDocumentResult result = signerClient.createDocument(documentRequest).get(0);
        
        System.out.println(String.format("Document %s created", result.getDocumentId().toString()));
    }
}