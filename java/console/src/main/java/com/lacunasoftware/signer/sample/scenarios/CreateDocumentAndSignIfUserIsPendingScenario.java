package com.lacunasoftware.signer.sample.scenarios;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.lacunasoftware.signer.ActionStatus;
import com.lacunasoftware.signer.DocumentFilterStatus;
import com.lacunasoftware.signer.DocumentStatus;
import com.lacunasoftware.signer.FileUploadModel;
import com.lacunasoftware.signer.FlowActionType;
import com.lacunasoftware.signer.PaginationOrders;
import com.lacunasoftware.signer.documents.ActionUrlRequest;
import com.lacunasoftware.signer.documents.ActionUrlResponse;
import com.lacunasoftware.signer.documents.CreateDocumentRequest;
import com.lacunasoftware.signer.documents.CreateDocumentResult;
import com.lacunasoftware.signer.documents.DocumentListModel;
import com.lacunasoftware.signer.documents.DocumentModel;
import com.lacunasoftware.signer.flowactions.FlowActionCreateModel;
import com.lacunasoftware.signer.flowactions.FlowActionModel;
import com.lacunasoftware.signer.javaclient.PublicStartSignatureRequest;
import com.lacunasoftware.signer.javaclient.builders.FileUploadModelBuilder;
import com.lacunasoftware.signer.javaclient.exceptions.RestException;
import com.lacunasoftware.signer.javaclient.models.UploadModel;
import com.lacunasoftware.signer.javaclient.params.DocumentListParameters;
import com.lacunasoftware.signer.javaclient.requests.CompleteSignatureRequest;
import com.lacunasoftware.signer.javaclient.responses.CompleteSignatureResponse;
import com.lacunasoftware.signer.javaclient.responses.PaginatedSearchResponse;
import com.lacunasoftware.signer.javaclient.responses.StartSignatureResponse;
import com.lacunasoftware.signer.sample.Util;
import com.lacunasoftware.signer.users.ParticipantUserModel;

public class CreateDocumentAndSignIfUserIsPendingScenario extends Scenario {

    @Override
    public void Run() throws IOException, RestException, Exception {
        // 1. The file's bytes must be read by the application and uploaded
        byte[] content = Util.getInstance().getResourceFile("sample.pdf");
        UploadModel uploadModel = signerClient.uploadFile("sample.pdf", content, "application/pdf");

        // 2. Define the name of the document which will be visible in the application
        FileUploadModelBuilder fileUploadModelBuilder = new FileUploadModelBuilder(uploadModel);
        fileUploadModelBuilder.setDisplayName("Sign if user is pending signature sample");

        // 3. For each participant on the flow, create one instance of
        // ParticipantUserModel
        ParticipantUserModel user = new ParticipantUserModel();
        user.setName("Mr Robot");
        user.setEmail("mr.robot@mailinator.com");
        user.setIdentifier("16155907064");

        // 4. Create a FlowActionCreateModel instance for each action (signature or
        // approval) in the flow.
        // This object is responsible for defining the personal data of the participant
        // and the type of
        // action that he will perform on the flow
        FlowActionCreateModel flowActionCreateModel = new FlowActionCreateModel();
        flowActionCreateModel.setType(FlowActionType.SIGNER);
        flowActionCreateModel.setUser(user);

        // 5. Send the document create request. Set the NewFolderName property to create
        // a folder for the document.
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

        UUID docUuid = result.getDocumentId();
        System.out.println(String.format("Document %s created", docUuid.toString()));

        // If you wish to search for a specific document or search multiple documents
        // within an organization
        // we offer multiple ways to do so. For this example, we'll search for the
        // recently created document
        // and sign it, but it is possible to search for multiple documents if there's more than 1 pending.
        // see DocumentListScenario or check the GET api/documents endpoint for more
        // information about document queries
        // https://signer-lac.azurewebsites.net/swagger/index.html

        // Define the arguments to list the documents according to your requirements
        DocumentListParameters searchParams = new DocumentListParameters();
        searchParams.setOrder(PaginationOrders.DESC); // Order the list: Ascending = Oldest on top / Descending = Newest
                                                      // on top
        searchParams.setLimit(10); // Define the number of Documents that you want to list
        searchParams.setDocumentFilterStatus(DocumentFilterStatus.PENDING); // List documents according to its status
        searchParams.setQ("sample"); // Retrieve the document list by name

        // Call the ListDocuments method and pass "searchParams" as a parameter
        PaginatedSearchResponse<DocumentListModel> documentListResponse = signerClient.listDocuments(searchParams);

        for (DocumentListModel test : documentListResponse.getItems()) {
            docUuid = test.getId();
            // Create the action URL
            ActionUrlRequest actionUrlRequest = new ActionUrlRequest()
                    .emailAddress(user.getEmail())
                    .identifier(user.getIdentifier());
            ActionUrlResponse response = signerClient.getActionUrl(docUuid, actionUrlRequest);
            String actionUrl = response.getEmbedUrl();
            System.out.println(String.format("Found document of id %s, checking if document has pending signatures",
                    test.getId()));
            // 6. Now we'll retrieve the document and see if the user is pending signature
            DocumentModel documentDetails = signerClient.getDocumentDetails(test.getId());
            // If it is not completed, then at least 1 signature is pending
            if (documentDetails.getStatus() == DocumentStatus.PENDING) {
                System.out.println(String.format("Document status is %s\nSigning document id %s... ",
                        documentDetails.getStatus().toString(), test.getId()));
                for (FlowActionModel flowAction : documentDetails.getFlowActions()) {
                    // get all flow actions which are pending
                    if (flowAction.getStatus() == ActionStatus.PENDING) {
                        String identifier = flowAction.getUser().getIdentifier(); // Get user identifier (CPF)
                        String certificatePassword = "1234"; // Default password for lacuna's test certificates
                        KeyStore keyStore = loadKeyStore(identifier, certificatePassword);
                        // Retrieve user certificate based on identifier
                        X509Certificate certificate = getCertificate(keyStore);
                        PrivateKey pKey = getPrivateKey(keyStore, certificatePassword);
                        // 7. Perform the server side signature
                        performServerSideSignature(certificate, actionUrl, pKey);
                    }
                }
            }
        }

    }

    private void performServerSideSignature(X509Certificate certificate, String actionUrl, PrivateKey pKey)
            throws Exception {
        // Replace the URL target endpoint path
        actionUrl.replace("/document/key/", "/sign/");
        URL url = new URL(actionUrl);
        // Get the query part of the URL
        String query = url.getQuery();

        // Parse the query string
        Map<String, String> queryParams = parseQuery(query);
        // Parse the document id
        String documentKey = extractDocumentKey(url.getPath());

        // Retrieve the "ticket" parameter
        String ticket = queryParams.get("ticket");
        // System.out.println("Ticket: " + ticket);

        // Now we'll start a signature, receiving a hash which must be signed
        PublicStartSignatureRequest request = new PublicStartSignatureRequest();
        // encode cert and insert in request
        request.setCertificate(certificate.getEncoded());
        request.setTicket(ticket);
        StartSignatureResponse response = signerClient.startPublicSignature(documentKey, request);
        // sign the hash
        byte[] signature = signHash(pKey, response.getToSignHash());

        // verify signature using native checking
        boolean isValid = verifySignature(certificate, response.getToSignHash(), signature);

        // Complete the signature process
        if (isValid) {
            // Prepare the completion request and send it
            CompleteSignatureRequest completeSigRequest = new CompleteSignatureRequest();
            completeSigRequest.setSignature(signature);
            completeSigRequest.setToken(response.getToken()); // the token obtained from the start request
            CompleteSignatureResponse completeSigResponse = signerClient.completePublicSignature(documentKey,
                    completeSigRequest);

            if (completeSigResponse.isSuccess()) {
                // 8. Get the verification URL
                String verificationUrl = "https://signer-lac.azurewebsites.net/validate/" + documentKey;
                System.out.println("Signature completion succesful!\nVerification URL is:");
                System.out.println(verificationUrl);
                System.out.println("Generate a QR code from the URL above");
            } else {
                System.out.println("Signature completion failed");
            }
        }
    }

    private boolean verifySignature(X509Certificate certificate, byte[] toSignHash, byte[] signature)
            throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        Signature signatureInstance = Signature.getInstance("NoneWithRSA");
        signatureInstance.initVerify(certificate.getPublicKey());
        signatureInstance.update(toSignHash);
        boolean isValid = signatureInstance.verify(signature);
        return isValid;
    }

    private Map<String, String> parseQuery(String query) throws Exception {
        Map<String, String> queryParams = new HashMap<>();

        if (query != null && !query.isEmpty()) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                String key = URLDecoder.decode(keyValue[0], "UTF-8");
                String value = keyValue.length > 1 ? URLDecoder.decode(keyValue[1], "UTF-8") : "";
                queryParams.put(key, value);
            }
        }

        return queryParams;
    }

    private String extractDocumentKey(String path) {
        String prefix = "/document/key/";
        String suffix = "/sign";
        if (path.contains(prefix) && path.contains(suffix)) {
            int startIndex = path.indexOf(prefix) + prefix.length();
            int endIndex = path.indexOf(suffix);
            return path.substring(startIndex, endIndex);
        }
        return null;
    }

    private byte[] signHash(PrivateKey pKey, byte[] toSignHash) throws Exception {
        Signature signatureInstance = Signature.getInstance("NoneWithRSA");
        try {
            signatureInstance.initSign(pKey);
            signatureInstance.update(toSignHash);
            return signatureInstance.sign();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    // Function to load the KeyStore
    private KeyStore loadKeyStore(String identifier, String password) throws Exception {
        String pfxFilePath = identifier + ".pfx";
        try (FileInputStream fis = new FileInputStream(Util.getInstance().getResourceFileAsString(pfxFilePath))) {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(fis, password.toCharArray());
            return keyStore;
        }
    }

    // Function to retrieve the private key
    private PrivateKey getPrivateKey(KeyStore keyStore, String password) throws Exception {
        String alias = keyStore.aliases().nextElement();
        return (PrivateKey) keyStore.getKey(alias, password.toCharArray());
    }

    // Function to retrieve the certificate
    private X509Certificate getCertificate(KeyStore keyStore) throws Exception {
        String alias = keyStore.aliases().nextElement();
        return (X509Certificate) keyStore.getCertificate(alias);
    }

}
