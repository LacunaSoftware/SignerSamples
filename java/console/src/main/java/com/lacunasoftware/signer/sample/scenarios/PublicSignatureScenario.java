package com.lacunasoftware.signer.sample.scenarios;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lacunasoftware.signer.FileUploadModel;
import com.lacunasoftware.signer.FlowActionType;
import com.lacunasoftware.signer.documents.ActionUrlRequest;
import com.lacunasoftware.signer.documents.ActionUrlResponse;
import com.lacunasoftware.signer.documents.CreateDocumentRequest;
import com.lacunasoftware.signer.documents.CreateDocumentResult;
import com.lacunasoftware.signer.flowactions.FlowActionCreateModel;
import com.lacunasoftware.signer.javaclient.PublicStartSignatureRequest;
import com.lacunasoftware.signer.javaclient.builders.FileUploadModelBuilder;
import com.lacunasoftware.signer.javaclient.exceptions.RestException;
import com.lacunasoftware.signer.javaclient.models.UploadModel;
import com.lacunasoftware.signer.javaclient.requests.CompleteSignatureRequest;
import com.lacunasoftware.signer.javaclient.responses.StartSignatureResponse;
import com.lacunasoftware.signer.sample.Util;
import com.lacunasoftware.signer.users.ParticipantUserModel;

public class PublicSignatureScenario extends Scenario {

    /**
     * This scenario demonstrates the public certificate-based digital signature flow
     * using startPublicSignature / completePublicSignature.
     *
     * The signer is identified through the document's public key (obtained from the
     * embed/action URL) rather than the document UUID. This is the typical integration
     * pattern when embedding signing inside an external application.
     *
     * Steps:
     *   1. Create a document with one signer.
     *   2. Obtain the embed URL via getActionUrl and extract the document key.
     *   3. Call startPublicSignature with the signer's certificate — the server returns
     *      the hash to be signed and a token.
     *   4. Sign the hash with the signer's private key (PKI operation — see stub below).
     *   5. Call completePublicSignature with the signature bytes and the token.
     */
    @Override
    public void Run() throws IOException, RestException, Exception {

        // 1. Create the document
        CreateDocumentResult documentResult = createDocumentForSigning();
        System.out.println(String.format("Document %s created", documentResult.getDocumentId()));

        // 2. Get the action URL and extract the document key
        ActionUrlRequest actionUrlRequest = new ActionUrlRequest();
        actionUrlRequest.setEmailAddress("jack.bauer@mailinator.com");
        actionUrlRequest.setIdentifier("75502846369");

        ActionUrlResponse actionUrlResponse = signerClient.getActionUrl(documentResult.getDocumentId(), actionUrlRequest);
        String key = extractKey(actionUrlResponse.getEmbedUrl());
        System.out.println(String.format("Document key: %s", key));

        // 3. Load the signer's DER-encoded X.509 certificate.
        //    In a real application this comes from the user's hardware token, smart card,
        //    or browser certificate selection API — not from a local file.
        byte[] certificateBytes = Util.getInstance().getResourceFile("certificate.cer");

        PublicStartSignatureRequest startRequest = new PublicStartSignatureRequest();
        startRequest.setCertificate(certificateBytes);

        StartSignatureResponse startResponse = signerClient.startPublicSignature(key, startRequest);
        System.out.println(String.format("Signature started, digest algorithm: %s", startResponse.getDigestAlgorithm()));

        // 4. Sign the hash with the private key matching the certificate loaded above.
        //    The hash bytes and digest algorithm come from the server response.
        byte[] signature = performSignature(startResponse.getToSignHash(), startResponse.getDigestAlgorithm());

        // 5. Complete the public signature
        CompleteSignatureRequest completeRequest = new CompleteSignatureRequest();
        completeRequest.setSignature(signature);
        completeRequest.setToken(startResponse.getToken());

        signerClient.completePublicSignature(key, completeRequest);
        System.out.println("Public signature completed successfully");
    }

    private CreateDocumentResult createDocumentForSigning() throws IOException, RestException {
        byte[] content = Util.getInstance().getResourceFile("sample.pdf");
        UploadModel uploadModel = signerClient.uploadFile("sample.pdf", content, "application/pdf");

        FileUploadModelBuilder fileUploadModelBuilder = new FileUploadModelBuilder(uploadModel);
        fileUploadModelBuilder.setDisplayName("Public Signature Sample");

        ParticipantUserModel user = new ParticipantUserModel();
        user.setName("Jack Bauer");
        user.setEmail("jack.bauer@mailinator.com");
        user.setIdentifier("75502846369");

        FlowActionCreateModel flowAction = new FlowActionCreateModel();
        flowAction.setType(FlowActionType.SIGNER);
        flowAction.setUser(user);

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
                add(flowAction);
            }
        });

        return signerClient.createDocument(documentRequest).get(0);
    }

    private String extractKey(String embedUrl) {
        Pattern pattern = Pattern.compile("/key/([^/?]+)");
        Matcher matcher = pattern.matcher(embedUrl);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("Could not extract document key from URL: " + embedUrl);
    }

    /**
     * Signs the hash with the signer's private key.
     *
     * Replace this stub with actual cryptographic signing. Example using Java Security:
     *
     *   Signature sig = Signature.getInstance("SHA256withRSA");
     *   sig.initSign(privateKey);
     *   sig.update(toSignHash);
     *   return sig.sign();
     *
     * @param toSignHash      the bytes returned by startPublicSignature that must be signed
     * @param digestAlgorithm the hash algorithm name (e.g. "SHA256")
     * @return the raw signature bytes to send in completePublicSignature
     */
    private byte[] performSignature(byte[] toSignHash, String digestAlgorithm) {
        // TODO: replace with real private-key signing using the token/smart card driver.
        throw new UnsupportedOperationException(
            "Replace this stub with actual private-key signing for algorithm: " + digestAlgorithm);
    }
}
