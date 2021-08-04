package com.lacunasoftware.signer.sample.scenarios;

import com.lacunasoftware.signer.documents.CancelDocumentRequest;
import com.lacunasoftware.signer.documents.CreateDocumentResult;
import com.lacunasoftware.signer.javaclient.exceptions.RestException;

import java.io.IOException;
import java.util.UUID;

public class CancelDocumentScenario extends Scenario {
    /**
     * This scenario demonstrates how to cancel a document as an organization application.
     */
    @Override
    public void Run() throws IOException, RestException {

        //1 - You need a document id
        CreateDocumentResult document = createDocument();
        UUID docId = document.getDocumentId();

        //2 - Create a cancellation request and give it a reason
        CancelDocumentRequest cancelDocument = new CancelDocumentRequest().reason("This is a document cancellation");

        //3 - Send the cancellation request
        try {
            signerClient.cancelDocument(docId, cancelDocument);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }
}
