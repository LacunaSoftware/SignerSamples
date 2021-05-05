package com.lacunasoftware.signer.sample.scenarios;

import com.lacunasoftware.signer.FileUploadModel;
import com.lacunasoftware.signer.FlowActionType;
import com.lacunasoftware.signer.attachments.AttachmentUploadModel;
import com.lacunasoftware.signer.documents.CreateDocumentRequest;
import com.lacunasoftware.signer.documents.CreateDocumentResult;
import com.lacunasoftware.signer.flowactions.FlowActionCreateModel;
import com.lacunasoftware.signer.javaclient.builders.AttachmentUploadBuilder;
import com.lacunasoftware.signer.javaclient.builders.FileUploadModelBuilder;
import com.lacunasoftware.signer.javaclient.exceptions.RestException;
import com.lacunasoftware.signer.javaclient.models.UploadModel;
import com.lacunasoftware.signer.sample.Util;
import com.lacunasoftware.signer.users.ParticipantUserModel;

import java.io.IOException;
import java.util.ArrayList;

public class CreateDocumentWithAttachmentScenario extends Scenario {
    /**
     * This scenario demonstrates the creation of a document with Attachment.
     */
    @Override
    public void Run() throws IOException, RestException {
        // 1. The file's bytes must be read by the application and uploaded
        byte[] content = Util.getInstance().getResourceFile("sample.pdf");
        UploadModel uploadModel = signerClient.uploadFile("sample.pdf", content, "application/pdf");

        // 2. Define the name of the document which will be visible in the application
        FileUploadModelBuilder fileUploadModelBuilder = new FileUploadModelBuilder(uploadModel);
        fileUploadModelBuilder.setDisplayName("One Signer Sample");


        // 3. Repeat the same steps above but now for the attachment and using AttachmentUploadBuilder
        byte[] attachmentContent = Util.getInstance().getResourceFile("sample.pdf");
        UploadModel attachmentModel = signerClient.uploadFile("sample.pdf", attachmentContent, "application/pdf");
        AttachmentUploadBuilder attachmentFileUploadModelBuilder = new AttachmentUploadBuilder(attachmentModel);
        attachmentFileUploadModelBuilder.setDisplayName("One Attachment Sample");
        //here you can define if the attachment will be restrict to the organization participants (true) or to everyone in the document flow (false)
        attachmentFileUploadModelBuilder.setIsPrivate(false);


        // 4. For each participant on the flow, create one instance of ParticipantUserModel
        ParticipantUserModel user = new ParticipantUserModel();
        user.setName("Jack Bauer");
        user.setEmail("jack.bauer@mailinator.com");
        user.setIdentifier("75502846369");

        // 5. Create a FlowActionCreateModel instance for each action
        FlowActionCreateModel flowActionCreateModel = new FlowActionCreateModel();
        flowActionCreateModel.setType(FlowActionType.SIGNER);
        flowActionCreateModel.setUser(user);

        // 6. Send the document create request with attachment's attribute
        CreateDocumentRequest documentRequest = new CreateDocumentRequest();
        documentRequest.setFiles(new ArrayList<FileUploadModel>() {
            private static final long serialVersionUID = 1L;
            {
                add(fileUploadModelBuilder.toModel());
            }
        });
        documentRequest.setAttachments(new ArrayList<AttachmentUploadModel>(){
            private static final long serialVersionUID = 1L;
            {
                add(attachmentFileUploadModelBuilder.toModel());
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
