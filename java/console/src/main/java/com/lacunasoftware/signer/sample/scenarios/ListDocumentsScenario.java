package com.lacunasoftware.signer.sample.scenarios;

import com.lacunasoftware.signer.PaginationOrders;
import com.lacunasoftware.signer.documents.DocumentListModel;
import com.lacunasoftware.signer.documents.DocumentModel;
import com.lacunasoftware.signer.javaclient.exceptions.RestException;
import com.lacunasoftware.signer.javaclient.params.DocumentListParameters;
import com.lacunasoftware.signer.javaclient.responses.PaginatedSearchResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListDocumentsScenario extends  Scenario{

    @Override
    public void Run() throws IOException, RestException {

        /**
         * This scenario describes how to list documents.
         */

        //1 - define the arguments to list the documents according to your requirements
        DocumentListParameters searchParams = new DocumentListParameters();
            searchParams.setOrder(PaginationOrders.DESC); //Order the list: Ascending = Oldest on top / Descending = Newest on top
            searchParams.setLimit(10); //Define the number of Documents that you want to list
            searchParams.setIsConcluded(false); // List concluded documents if it's "true"
            searchParams.setQ("Doc1"); //Retrieve the document list by name

        //2 - Call the ListDocuments method and pass "searchParams" as a parameter
        PaginatedSearchResponse<DocumentListModel> response = signerClient.listDocuments(searchParams);


        for (DocumentListModel test: response.getItems()) {
          System.out.println(test.getName());
        }

    }
}
