package com.lacunasoftware.signer.sample.scenarios;

import com.lacunasoftware.signer.FlowActionType;
import com.lacunasoftware.signer.documents.GenerateDocumentRequest;
import com.lacunasoftware.signer.documents.GenerationDocumentResult;
import com.lacunasoftware.signer.flowactions.FlowActionCreateModel;
import com.lacunasoftware.signer.javaclient.builders.FileUploadModelBuilder;
import com.lacunasoftware.signer.javaclient.exceptions.RestException;
import com.lacunasoftware.signer.javaclient.models.UploadModel;
import com.lacunasoftware.signer.sample.Util;
import com.lacunasoftware.signer.users.ParticipantUserModel;

import java.io.IOException;
import java.util.ArrayList;

public class GenerateDocumentWithTwoOrMoreSignersScenario extends Scenario {

    /**
     * This scenario demonstrates the creation of a document with
     * Prepositioned signatures.
     */
    @Override
    public void Run() throws IOException, RestException {
        // 1. The PDF Form file's bytes must be read by the application and uploaded
        byte[] pdfContent = Util.getInstance().getResourceFile("Contrato-Servicos.pdf");
        UploadModel pdfUploadModel = signerClient.uploadFile("Contrato-Servicos.pdf", pdfContent, "application/pdf");
        // 2. Define the name of the document which will be visible in the application
        FileUploadModelBuilder pdfFileUploadModelBuilder = new FileUploadModelBuilder(pdfUploadModel);
        pdfFileUploadModelBuilder.setDisplayName("PDF for Generate Document with two or more signers sample");

        // 3. The CSV file's bytes must be read by the application and uploaded
        byte[] csvContent = Util.getInstance().getResourceFile("Contrato-Servicos.csv");
        UploadModel csvUploadModel = signerClient.uploadFile("Contrato-Servicos.csv", csvContent, "text/csv");
        // 4. Define the name of the document which will be visible in the application
        FileUploadModelBuilder csvFileUploadModelBuilder = new FileUploadModelBuilder(csvUploadModel);
        csvFileUploadModelBuilder.setDisplayName("CSV for Generate Document with two or more signers sample");

        // 5. For each participant on the flow, create one instance of
        // ParticipantUserModel
        ParticipantUserModel user = new ParticipantUserModel();
        user.setName("Jack Bauer");
        user.setEmail("jack.bauer@mailinator.com");
        user.setIdentifier("75502846369");

        // 6. Create another instance for a second user
        ParticipantUserModel userTwo = new ParticipantUserModel();
        userTwo.setName("James Bond");
        userTwo.setEmail("james.bond@mailinator.com");
        userTwo.setIdentifier("95588148061");

        // 8. Create a FlowActionCreateModel instance for each action (signature or
        // approval) in the flow.
        // This object is responsible for defining the personal data of the participant,
        // the type of
        // action that he will perform on the flow and the order in which this action
        // will take place
        // (Step property) and the pre-positioned marks for placing signatures. If the
        // Step property of all action are the same or not specified they
        // may be executed at any time
        FlowActionCreateModel flowActionCreateModel = new FlowActionCreateModel();
        flowActionCreateModel.setType(FlowActionType.SIGNER);
        flowActionCreateModel.setUser(user);

        FlowActionCreateModel flowActionCreateModelUserTwo = new FlowActionCreateModel();
        flowActionCreateModelUserTwo.setType(FlowActionType.SIGNER);
        flowActionCreateModelUserTwo.setUser(userTwo);

        // 9. Create a generation document request
        GenerateDocumentRequest generateDocumentRequest = new GenerateDocumentRequest();
        // 9.1 Set template file
        generateDocumentRequest.setTemplateFile(pdfFileUploadModelBuilder);
        // 9.2 Set data file
        generateDocumentRequest.setDataFile(csvFileUploadModelBuilder);
        // 9.3 Set flow actions
        generateDocumentRequest.setFlowActions(new ArrayList<FlowActionCreateModel>() {
            {
                add(flowActionCreateModel);
                add(flowActionCreateModelUserTwo);
            }
        });

        GenerationDocumentResult result = signerClient.generateDocument(generateDocumentRequest);

        System.out.println(String.format("Document %s created", result.getId().toString()));
    }
}
