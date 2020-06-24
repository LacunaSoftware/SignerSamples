package com.lacunasoftware.signer.sample.scenarios;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import com.lacunasoftware.signer.CreateDocumentRequest;
import com.lacunasoftware.signer.FileUploadModel;
import com.lacunasoftware.signer.FlowActionCreateModel;
import com.lacunasoftware.signer.FlowActionType;
import com.lacunasoftware.signer.FolderCreateRequest;
import com.lacunasoftware.signer.FolderInfoModel;
import com.lacunasoftware.signer.ParticipantUserModel;
import com.lacunasoftware.signer.RestException;
import com.lacunasoftware.signer.UploadModel;
import com.lacunasoftware.signer.sample.Util;

public class CreateDocumentInFolderScenario extends Scenario {
    /**
    * This scenario shows step-by-step the submission of a document
    * into an already existing folder.
    */
    @Override
    public void Run() throws IOException, RestException {
        // 1. The file's bytes must be read by the application and uploaded using the method UploadFileAsync.
        byte[] content = Util.getInstance().getResourceFile("sample.pdf");
		UploadModel uploadModel = signerClient.uploadFile("sample.pdf", content, "application/pdf");

        // 2. Signer's server expects a FileUploadModel's list to create a document.
        FileUploadModel fileUploadModel = new FileUploadModel(uploadModel);
        fileUploadModel.setDisplayName("Doc in Folder " + OffsetDateTime.now(ZoneOffset.UTC).toString());
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

        // 6. You'll need to request the creation of a folder or get an existing one.
        FolderCreateRequest folderCreateRequest = new FolderCreateRequest();
        folderCreateRequest.setName("Folder " + OffsetDateTime.now(ZoneOffset.UTC).toString());
        FolderInfoModel folderInfoModel = signerClient.createFolder(folderCreateRequest);

        // 7. To create the document request, use the list of FileUploadModel and the list of FlowActionCreateModel.
        //    Also, it's necessary to provide the id of the folder where you wish to save the document.
        //    It's also possible to create a brand new folder by lefting the `FolderId` property as null and assigning 
        //    the occult propertie `NewFolderName` with the desired name for the folder.
        CreateDocumentRequest documentRequest = new CreateDocumentRequest();
        documentRequest.setFiles(fileUploadModelList);
        documentRequest.setFlowActions(flowActionCreateModelList);
        documentRequest.setFolderId(folderInfoModel.getId());
        signerClient.createDocument(documentRequest);
    }
}