package com.lacunasoftware.signer.sample.scenarios;

import com.lacunasoftware.signer.DocumentDownloadTypes;
import com.lacunasoftware.signer.DocumentTicketType;
import com.lacunasoftware.signer.TicketModel;
import com.lacunasoftware.signer.documents.CreateDocumentResult;
import com.lacunasoftware.signer.javaclient.exceptions.RestException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class DownloadDocumentVersionScenario extends Scenario{

    @Override
    public void Run() throws IOException, RestException {

        // 1. Get a document Id
        CreateDocumentResult result = createDocument();

        //2. You can get a ticket to a specific version of the document. The ticket is a temporary URL that allows you to download that version.
        TicketModel ticketDownload =  signerClient.getDocumentDownloadTicket(result.getDocumentId(), DocumentTicketType.ORIGINAL);

        // 3. Get the document by passing it's Id and the Ticket type
        // Be sure to select the exact DocumentTicketType to download the type of document you want.
        // Check the available types by inspecting DocumentTicketType's ENUM.
        InputStream documentVersion = signerClient.getDocumentContent(result.getDocumentId(), DocumentDownloadTypes.ORIGINAL);

        saveFileStream(documentVersion);

        // 4. You can also get the bytes directly instead of a Stream for a specific version type of the document
        byte[] documentVersionBytes = signerClient.getDocumentBytes(result.getDocumentId(), DocumentTicketType.ORIGINAL);

    }


    private void saveFileStream(InputStream stream)
    {
        try {
            Files.copy(stream, Paths.get("sample.pdf"), StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
