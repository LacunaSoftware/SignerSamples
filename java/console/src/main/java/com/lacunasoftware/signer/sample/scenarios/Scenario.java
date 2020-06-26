package com.lacunasoftware.signer.sample.scenarios;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.lacunasoftware.signer.CreateDocumentRequest;
import com.lacunasoftware.signer.CreateDocumentResult;
import com.lacunasoftware.signer.FileUploadModel;
import com.lacunasoftware.signer.FlowActionCreateModel;
import com.lacunasoftware.signer.FlowActionType;
import com.lacunasoftware.signer.ParticipantUserModel;
import com.lacunasoftware.signer.RestException;
import com.lacunasoftware.signer.SignerClient;
import com.lacunasoftware.signer.UploadModel;
import com.lacunasoftware.signer.sample.Util;

public abstract class Scenario {

    protected SignerClient signerClient;

    public void Init() throws URISyntaxException {
        String domain = "https://signer-lac.azurewebsites.net";
        String token = "API Sample App|43fc0da834e48b4b840fd6e8c37196cf29f919e5daedba0f1a5ec17406c13a99";
		signerClient = new SignerClient(domain, token);
    }

    // Creates a generic document, useful for certain scenarios.
    public abstract void Run() throws IOException, RestException, Exception;

    protected CreateDocumentResult createDocument() throws IOException, RestException {
        byte[] content = Util.getInstance().getResourceFile("sample.pdf");
        UploadModel uploadModel = signerClient.uploadFile("sample.pdf", content, "application/pdf");
        
        FileUploadModel fileUploadModel = new FileUploadModel(uploadModel);
        fileUploadModel.setDisplayName("Check Status Sample");

        ParticipantUserModel user = new ParticipantUserModel();
		user.setName("Jack Bauer");
		user.setEmail("jack.bauer@mailinator.com");
        user.setIdentifier("75502846369");

        FlowActionCreateModel flowActionCreateModel = new FlowActionCreateModel();
        flowActionCreateModel.setType(FlowActionType.SIGNER);
        flowActionCreateModel.setUser(user);

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
                add(flowActionCreateModel);
            }          
        });
        List<CreateDocumentResult> documentResults = signerClient.createDocument(documentRequest);

        return documentResults.get(0);
    }
}