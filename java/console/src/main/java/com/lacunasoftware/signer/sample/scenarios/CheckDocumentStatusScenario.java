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

public class CheckDocumentStatusScenario extends Scenario {
    /**
    * This scenario shows step-by-step the submission of a document
    * and the process to check if the flow's actions were completed.
    */
    @Override
    public void Run() throws IOException, RestException {
        // 1. The file's bytes must be read by the application and uploaded using the method UploadFileAsync.
        byte[] content = Util.getInstance().getResourceFile("sample.pdf");
		UploadModel uploadModel = signerClient.uploadFile("sample.pdf", content, "application/pdf");

        // 2. Signer's server expects a FileUploadModel's list to create a document.
        FileUploadModel fileUploadModel = new FileUploadModel(uploadModel);
        fileUploadModel.setDisplayName("Verify " + OffsetDateTime.now(ZoneOffset.UTC).toString());
        List<FileUploadModel> fileUploadModelList = new ArrayList<>();
		fileUploadModelList.add(fileUploadModel);

        // 3. Foreach participant on the flow, you'll need to create an instance of ParticipantUserModel.
        ParticipantUserModel user = new ParticipantUserModel();
		user.setName("Jack Bauer");
		user.setEmail("jack.bauer@mailinator.com");
        user.setIdentifier("75502846369");
        
        // 4. You'll need to create a FlowActionCreateModel's instance foreach ParticipantUserModel
        //    created in the previous step. The FlowActionCreateModel is responsible for holding
        //    the personal data of the participant and the type of action that it will peform on the flow.
        FlowActionCreateModel flowActionCreateModel = new FlowActionCreateModel();
        flowActionCreateModel.setType(FlowActionType.SIGNER);
        flowActionCreateModel.setUser(user);

        // 5. Signer's server expects a FlowActionCreateModel's list to create a document.
        List<FlowActionCreateModel> flowActionCreateModelList = new ArrayList<>();
        flowActionCreateModelList.add(flowActionCreateModel);

        // 6. To create the document request, use the list of FileUploadModel and the list of FlowActionCreateModel.
        CreateDocumentRequest documentRequest = new CreateDocumentRequest();
        documentRequest.setFiles(fileUploadModelList);
        documentRequest.setFlowActions(flowActionCreateModelList);
        List<CreateDocumentResult> documentResults = signerClient.createDocument(documentRequest);

        /**
        * NOTE: The following way of verifying the concludeness of the flow 
        * works but is discouraged, it will have a huge computational cost not worthy
        * for such a simple task.
        * 
        * The best way to do this task is by enabling a webhook in your organization on the
        * signer instance. Whenever the flow is completed the instance will take care of
        * notifying your application by making a POST request for your previously registered url.
        * 
        * Access the following link for information about the data posted and search for Webhooks.DocumentConcludedModel:
        * https://signer-lac.azurewebsites.net/swagger/index.html
        */

        // 7. Check for the concludeness of the flow.
        for (CreateDocumentResult documentResult : documentResults) {
            DocumentModel details = signerClient.getDocumentDetails(documentResult.getDocumentId());

            // 7.1. Extracts details from the document.
            if (details.isConcluded()) {

            }

            // 7.3. Check if each flow action individualy is completed.
            for (FlowActionModel flowAction : details.getFlowActions()) {
                if (flowAction.getStatus() == ActionStatus.COMPLETED) {

                }
            }
        }
    }
}