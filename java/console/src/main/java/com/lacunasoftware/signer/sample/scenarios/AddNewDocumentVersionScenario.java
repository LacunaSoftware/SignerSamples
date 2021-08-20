package com.lacunasoftware.signer.sample.scenarios;

import com.lacunasoftware.signer.FileUploadModel;
import com.lacunasoftware.signer.FlowActionType;
import com.lacunasoftware.signer.documents.CreateDocumentRequest;
import com.lacunasoftware.signer.documents.CreateDocumentResult;
import com.lacunasoftware.signer.documents.DocumentAddVersionRequest;
import com.lacunasoftware.signer.flowactions.FlowActionCreateModel;
import com.lacunasoftware.signer.javaclient.builders.FileUploadModelBuilder;
import com.lacunasoftware.signer.javaclient.exceptions.RestException;
import com.lacunasoftware.signer.javaclient.models.UploadModel;
import com.lacunasoftware.signer.sample.Util;
import com.lacunasoftware.signer.users.ParticipantUserModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class AddNewDocumentVersionScenario extends Scenario {
    /**
     * This scenario demonstrates how to add a new document version to a existing document (the flow will be reset) .
     */
    @Override
    public void Run() throws IOException, RestException {

        // 1. You need a document id
        CreateDocumentResult document = createDocument();
        UUID docId = document.getDocumentId();

        // 2. The file's bytes must be read by the application and uploaded
        byte[] content = Util.getInstance().getResourceFile("sample.pdf");
        UploadModel uploadModel = signerClient.uploadFile("sample.pdf", content, "application/pdf");

        // 3. Define the name of the document which will be visible in the application
        FileUploadModelBuilder fileUploadModelBuilder = new FileUploadModelBuilder(uploadModel);
        fileUploadModelBuilder.setDisplayName("Add New Document Version Sample");

        // 3. Send the new version request
        DocumentAddVersionRequest documentAddVersionRequest = new DocumentAddVersionRequest();
        documentAddVersionRequest.setFile(fileUploadModelBuilder.toModel());

        signerClient.addNewDocumentVersion(docId, documentAddVersionRequest);


    }
}
