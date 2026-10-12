package com.lacunasoftware.signer.sample.scenarios;

import com.lacunasoftware.signer.DocumentDownloadTypes;
import com.lacunasoftware.signer.DocumentStatus;
import com.lacunasoftware.signer.DocumentTicketType;
import com.lacunasoftware.signer.TicketModel;
import com.lacunasoftware.signer.documents.DocumentModel;
import com.lacunasoftware.signer.javaclient.exceptions.RestException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class DownloadDocumentVersionScenario extends Scenario{

    // Signatures and PrinterFriendlyVersion exist only after at least one participant has signed.
    // To download them, set this to the id of a signed document of your organization, for instance
    // one whose status is Concluded. While it is null, the scenario creates a new document, which
    // nobody has signed yet, and downloads only its Original version.
    private static final String SIGNED_DOCUMENT_ID = null;

    /**
     * This scenario describes how to download documents: the original file, the signed file
     * (Signatures) and the printer-friendly version.
     */
    @Override
    public void Run() throws IOException, RestException {

        // 1. Get a document Id
        UUID documentId = SIGNED_DOCUMENT_ID != null ? UUID.fromString(SIGNED_DOCUMENT_ID) : createDocument().getDocumentId();

        //2. You can get a ticket to a specific version of the document. The ticket is a temporary URL (valid for 1 hour,
        //   no authentication needed) that allows you to download that version. Anyone with the URL can download it.
        TicketModel ticketDownload =  signerClient.getDocumentDownloadTicket(documentId, DocumentTicketType.ORIGINAL);

        // 3. Get the document by passing its Id and the version type.
        // Be sure to select the exact DocumentDownloadTypes to download the type of document you want.
        // Original: the file uploaded when the document was created. It is always available.
        InputStream original = signerClient.getDocumentContent(documentId, DocumentDownloadTypes.ORIGINAL);
        saveFileStream(original, "downloaded-original.pdf");

        // 4. You can also get the bytes directly instead of a Stream for a specific version type of the document
        byte[] documentVersionBytes = signerClient.getDocumentBytes(documentId, DocumentTicketType.ORIGINAL);

        // 5. For a document that nobody has signed yet, Signatures and PrinterFriendlyVersion fail with
        //    422 and the code DocumentHasNoSignatures. getDocumentContent reports any HTTP error as a
        //    RestUnreachableException, without the error code, so read the document first.
        DocumentModel details = signerClient.getDocumentDetails(documentId);
        if (!Boolean.TRUE.equals(details.isHasSignature())) {
            System.out.println(String.format("Document %s has no signatures yet, so only its Original version was downloaded.", documentId));
            return;
        }

        // 6. Signatures: the signed file. For a PDF signed with PAdES (the default) it is the PDF with the signatures
        //    embedded; for a CAdES signature it is a .p7s file. It holds the signatures collected so far: the document
        //    is final only when its status is Concluded. Canceled, refused and expired documents download only as
        //    Original and PrinterFriendlyVersion.
        DocumentStatus status = details.getStatus();
        if (status == DocumentStatus.CANCELED || status == DocumentStatus.REFUSED || status == DocumentStatus.EXPIRED) {
            System.out.println(String.format("Document %s is %s, so its signed file cannot be downloaded.", documentId, status.getValue()));
        } else {
            InputStream signed = signerClient.getDocumentContent(documentId, DocumentDownloadTypes.SIGNATURES);
            saveFileStream(signed, "downloaded-signed.pdf");
        }

        // 7. PrinterFriendlyVersion: a PDF with the signature marks and a signatures manifest appended.
        InputStream printerFriendly = signerClient.getDocumentContent(documentId, DocumentDownloadTypes.PRINTERFRIENDLYVERSION);
        saveFileStream(printerFriendly, "downloaded-printer-friendly.pdf");
    }


    private void saveFileStream(InputStream stream, String fileName) throws IOException
    {
        Path path = Paths.get(fileName);
        try (InputStream in = stream) {
            Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
        }
        System.out.println("Saved " + path.toAbsolutePath());
    }
}
