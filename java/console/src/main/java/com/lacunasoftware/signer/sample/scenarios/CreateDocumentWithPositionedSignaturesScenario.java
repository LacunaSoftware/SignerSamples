package com.lacunasoftware.signer.sample.scenarios;

import com.lacunasoftware.signer.DocumentMarkType;
import com.lacunasoftware.signer.FileUploadModel;
import com.lacunasoftware.signer.FlowActionType;
import com.lacunasoftware.signer.documentmark.PrePositionedDocumentMarkModel;
import com.lacunasoftware.signer.documents.CreateDocumentRequest;
import com.lacunasoftware.signer.documents.CreateDocumentResult;
import com.lacunasoftware.signer.flowactions.FlowActionCreateModel;
import com.lacunasoftware.signer.javaclient.builders.FileUploadModelBuilder;
import com.lacunasoftware.signer.javaclient.exceptions.RestException;
import com.lacunasoftware.signer.javaclient.models.UploadModel;
import com.lacunasoftware.signer.sample.Util;
import com.lacunasoftware.signer.users.ParticipantUserModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateDocumentWithPositionedSignaturesScenario extends Scenario {

    /**
     * This scenario demonstrates the creation of a document with
     * Prepositioned signatures.
     */
    @Override
    public void Run() throws IOException, RestException {
        // 1. The file's bytes must be read by the application and uploaded
        byte[] content = Util.getInstance().getResourceFile("sample.pdf");

        UploadModel uploadModel = signerClient.uploadFile("sample.pdf", content, "application/pdf");

        // 2. Define the name of the document which will be visible in the application
        FileUploadModelBuilder fileUploadModelBuilder = new FileUploadModelBuilder(uploadModel);
        fileUploadModelBuilder.setDisplayName("Prepositioned signatures");

        // 3. For each participant on the flow, create one instance of ParticipantUserModel
        ParticipantUserModel user = new ParticipantUserModel();
        user.setName("Jack Bauer");
        user.setEmail("jack.bauer@mailinator.com");
        user.setIdentifier("75502846369");

        // 4. Create a FlowActionCreateModel instance for each action (signature or approval) in the flow.
        //    This object is responsible for defining the personal data of the participant, the type of
        //    action that he will perform on the flow and the order in which this action will take place
        //    (Step property) and the pre-positioned marks for placing signatures. If the Step property of all action are the same or not specified they
        //    may be executed at any time
        FlowActionCreateModel flowActionCreateModel = new FlowActionCreateModel();

        flowActionCreateModel.setType(FlowActionType.SIGNER);
        flowActionCreateModel.setUser(user);

        PrePositionedDocumentMarkModel mark = new PrePositionedDocumentMarkModel()
                .type(DocumentMarkType.SIGNATUREVISUALREPRESENTATION) //This is the attribute responsible for defining the Type of signature you are going to use
                .uploadId(fileUploadModelBuilder.getId()) //Document id
                .topLeftX(395.0) //Signature position, in pixels, over the X axis
                .topLeftY(560.0) //Signature position, in pixels, over the Y axis
                .width(170.0)    //Width of the rectangular where signature will be placed in (It already has a default value)
                .height(94.0)    //Height of the rectangular where signature will be placed in (It already has a default value)
                .pageNumber(1);  //Page where the signature wil be placed

        //Adding the mark attributes to the list (you can preposition marks to different documents)
        List<PrePositionedDocumentMarkModel> listMark = new ArrayList<>();
        listMark.add(mark);

        flowActionCreateModel.setPrePositionedMarks(listMark);
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
                add(flowActionCreateModel);
            }
            });

        CreateDocumentResult result = signerClient.createDocument(documentRequest).get(0);

        System.out.println(String.format("Document %s created", result.getDocumentId().toString()));
    }
}
