package com.lacunasoftware.signer.sample.scenarios;

import com.lacunasoftware.signer.documents.CreateDocumentResult;
import com.lacunasoftware.signer.javaclient.exceptions.RestException;

import java.io.IOException;
import java.util.UUID;

public class DeleteDocumentScenario extends Scenario {
    /**
     * This scenario demonstrates how to delete a document using it's ID.
     */
    @Override
    public void Run() throws IOException, RestException {

        //1 - You need a document id
        CreateDocumentResult document = createDocument();
        UUID docId = document.getDocumentId();

        //2 - Call the api method to delete the document and pass the document Id as parameter
        signerClient.deleteDocument(docId);
    }
}
