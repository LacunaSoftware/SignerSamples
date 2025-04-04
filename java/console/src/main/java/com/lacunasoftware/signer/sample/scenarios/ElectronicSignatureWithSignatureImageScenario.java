package com.lacunasoftware.signer.sample.scenarios;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lacunasoftware.signer.FileUploadModel;
import com.lacunasoftware.signer.FlowActionType;
import com.lacunasoftware.signer.documents.ActionUrlRequest;
import com.lacunasoftware.signer.documents.ActionUrlResponse;
import com.lacunasoftware.signer.documents.CreateDocumentRequest;
import com.lacunasoftware.signer.documents.CreateDocumentResult;
import com.lacunasoftware.signer.flowactions.FlowActionCreateModel;
import com.lacunasoftware.signer.javaclient.builders.FileUploadModelBuilder;
import com.lacunasoftware.signer.javaclient.exceptions.RestException;
import com.lacunasoftware.signer.javaclient.models.UploadModel;
import com.lacunasoftware.signer.javaclient.requests.ElectronicSignatureRequest;
import com.lacunasoftware.signer.sample.Util;
import com.lacunasoftware.signer.uploads.ImageUploadModel;
import com.lacunasoftware.signer.users.ParticipantUserModel;

public class ElectronicSignatureWithSignatureImageScenario extends Scenario {
    @Override
    public void Run() throws IOException, RestException, Exception {
        // 0 Create a document as usual
        CreateDocumentResult result = CreateAndGetDocument();
        System.out.println(String.format("Document %s created", result.getDocumentId().toString()));

        // 1 Use action URL API as described in
        // https://docs.lacunasoftware.com/pt-br/articles/signer/integration-guide.html#assinatura-em-sua-pr%C3%B3pria-aplica%C3%A7%C3%A3o
        // Create the URL Request
        ActionUrlRequest actionUrlRequest = new ActionUrlRequest();
        actionUrlRequest.setEmailAddress("jack.bauer@mailinator.com");
        actionUrlRequest.setIdentifier("75502846369");

        // 1.1 Get Action URL
        ActionUrlResponse actionUrlResponse = signerClient.getActionUrl(result.getDocumentId(), actionUrlRequest);

        // 2 Extract document key and ticket from previous string
        Map<String, String> nMap = extractKeyAndTicket(actionUrlResponse.getEmbedUrl());
        System.out.println(nMap.get("key"));
        System.out.println(nMap.get("ticket"));

        // 3 Upload the user's signature using the Upload API in the same way it is done for the document.
        String signatureUploadId = uploadSignatureAndGetId("signature-pic");

        // 4 Call the POST /api/documents/keys/{key}/electronic-signature API
        // 4.1 First setup the request
        ElectronicSignatureRequest electronicSignatureRequest = new ElectronicSignatureRequest();
        electronicSignatureRequest.setTicket(nMap.get("ticket"));
        electronicSignatureRequest.setDisableNotifications(false);
        electronicSignatureRequest.setSignaturePosition(null);
        
        ImageUploadModel initialsImageModel = new ImageUploadModel();
        initialsImageModel.setId(signatureUploadId);
        initialsImageModel.setContentType("image/png");
        initialsImageModel.setSave(false); // in case you wanna save the signature image, set this to true
        electronicSignatureRequest.setInitialsImage(initialsImageModel);

        // Fill in only if you want to provide geolocation information. Otherwise, leave it as null.
        electronicSignatureRequest.setEvidences(null);

        //The user's timezone offset in minutes, this is the timezone that will appear on the signature's visual representation.
        //This offset is the difference from UTC to the user's time, that is, if the user's timezone is UTC-3 this offset should be -180.
        electronicSignatureRequest.setUserTimeZoneOffset(-180);

        signerClient.signElectronically(UUID.fromString(nMap.get("ticket")), electronicSignatureRequest);
    }

    private String uploadSignatureAndGetId(String location) throws IOException, RestException {
        byte[] content = Util.getInstance().getResourceFile("signature-pic.png");
        UploadModel uploadModel = signerClient.uploadFile("signature-pic.png", content, "image/png");

        return uploadModel.getId();
    }

    public static Map<String, String> extractKeyAndTicket(String url) {
        Map<String, String> result = new HashMap<>();
        Pattern pattern = Pattern.compile("/key/([^/]+)/.*\\?ticket=([^&]+)");
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            result.put("key", matcher.group(1));
            result.put("ticket", matcher.group(2));
        }
        return result;
    }

    private CreateDocumentResult CreateAndGetDocument() throws IOException, RestException {
        // 1. The file's bytes must be read by the application and uploaded
        byte[] content = Util.getInstance().getResourceFile("sample.pdf");
        UploadModel uploadModel = signerClient.uploadFile("sample.pdf", content, "application/pdf");

        // 2. Define the name of the document which will be visible in the application
        FileUploadModelBuilder fileUploadModelBuilder = new FileUploadModelBuilder(uploadModel);
        fileUploadModelBuilder.setDisplayName("One Signer Sample");

        // 3. For each participant on the flow, create one instance of
        // ParticipantUserModel
        ParticipantUserModel user = new ParticipantUserModel();
        user.setName("Jack Bauer");
        user.setEmail("jack.bauer@mailinator.com");
        user.setIdentifier("75502846369");

        // 4. Create a FlowActionCreateModel instance for each action (signature or
        // approval) in the flow.
        // This object is responsible for defining the personal data of the participant
        // and the type of
        // action that he will perform on the flow
        FlowActionCreateModel flowActionCreateModel = new FlowActionCreateModel();
        flowActionCreateModel.setType(FlowActionType.SIGNER);
        flowActionCreateModel.setUser(user);

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
        UUID docId = result.getDocumentId();
        return result;
    }
}
