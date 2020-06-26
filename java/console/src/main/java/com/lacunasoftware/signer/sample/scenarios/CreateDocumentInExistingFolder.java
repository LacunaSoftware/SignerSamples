package com.lacunasoftware.signer.sample.scenarios;

import java.util.ArrayList;

import com.lacunasoftware.signer.CreateDocumentRequest;
import com.lacunasoftware.signer.CreateDocumentResult;
import com.lacunasoftware.signer.FileUploadModel;
import com.lacunasoftware.signer.FlowActionCreateModel;
import com.lacunasoftware.signer.FlowActionType;
import com.lacunasoftware.signer.FolderInfoModel;
import com.lacunasoftware.signer.PaginatedSearchParams;
import com.lacunasoftware.signer.ParticipantUserModel;
import com.lacunasoftware.signer.UploadModel;
import com.lacunasoftware.signer.sample.Util;

public class CreateDocumentInExistingFolder extends Scenario {
    /**
     * This scenario demonstrates the creation of a document into an existing
     * folder.
     * 
     * @throws Exception
     */
    @Override
    public void Run() throws Exception {
        // 1. The file's bytes must be read by the application and uploaded
        byte[] content = Util.getInstance().getResourceFile("sample.pdf");
		UploadModel uploadModel = signerClient.uploadFile("sample.pdf", content, "application/pdf");

        // 2. Define the name of the document which will be visible in the application
        FileUploadModel fileUploadModel = new FileUploadModel(uploadModel);
        fileUploadModel.setDisplayName("Document in Existing Folder Sample");

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

        // 5. Search a folder by it's name
        PaginatedSearchParams paginatedSearchParams = new PaginatedSearchParams();
        paginatedSearchParams.setQ("Sample Folder");
        FolderInfoModel folder = 
            signerClient.listFoldersPaginated(paginatedSearchParams, null).getTotalCount() > 0 ? 
                signerClient.listFoldersPaginated(paginatedSearchParams, null).getItems().get(0) : null; 

        if (folder == null) {
            throw new Exception("Folder was not found");
        }

        // 6. Send the document create request
        CreateDocumentRequest documentRequest = new CreateDocumentRequest();
        documentRequest.setFolderId(folder.getId());
        documentRequest.setFiles(new ArrayList<FileUploadModel>() {
            private static final long serialVersionUID = 1L;
            {
                add(fileUploadModel);
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