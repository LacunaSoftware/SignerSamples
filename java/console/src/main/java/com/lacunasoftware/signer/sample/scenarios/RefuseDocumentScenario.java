package com.lacunasoftware.signer.sample.scenarios;

import com.lacunasoftware.signer.documents.CreateDocumentResult;
import com.lacunasoftware.signer.documents.DocumentModel;
import com.lacunasoftware.signer.flowactions.FlowActionModel;
import com.lacunasoftware.signer.javaclient.exceptions.RestException;
import com.lacunasoftware.signer.refusal.RefusalModel;
import com.lacunasoftware.signer.refusal.RefusalRequest;

import java.io.IOException;
import java.util.UUID;

public class RefuseDocumentScenario extends Scenario {
    /**
     * This scenario demonstrates how to refuse a document as an organization application.
     */
    @Override
    public void Run() throws IOException, RestException {

        //1 - You need a document id
        CreateDocumentResult document = createDocument();
        UUID docId = document.getDocumentId();

        //2 - Create a refusal request and give it a reason
        RefusalRequest refuseDocument = new RefusalRequest().reason("This is a document refusal");

        //3 - Send the refusal request
        try {
            signerClient.refuseDocument(docId, refuseDocument);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }
}