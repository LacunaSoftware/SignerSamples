<?php


namespace Lacuna\Scenarios;


use Lacuna\Signer\Model\DocumentDownloadTypes;
use Lacuna\Signer\Model\DocumentsDocumentContentModel;
use Lacuna\Signer\Model\DocumentsDocumentModel;
use Lacuna\Signer\Model\DocumentStatus;
use Lacuna\Signer\Model\DocumentTicketType;

class DownloadDocumentVersionScenario extends Scenario
{
    // Signatures and PrinterFriendlyVersion exist only after at least one participant has signed.
    // To download them, set this to the id of a signed document of your organization, for instance
    // one whose status is Concluded. While it is null, the scenario creates a new document, which
    // nobody has signed yet, and downloads only its Original version.
    const SIGNED_DOCUMENT_ID = null;

    /**
     * This scenario describes how to download documents: the original file, the signed file
     * (Signatures) and the printer-friendly version.
     */
    function run()
    {
        // 1. Get a document Id
        $documentId = self::SIGNED_DOCUMENT_ID ?: $this->createDocument()->getDocumentId();

        //2. You can get a ticket to a specific version of the document. The ticket is a temporary URL (valid for 1 hour,
        //   no authentication needed) that allows you to download that version. Anyone with the URL can download it.
        $ticketDownload = $this->signerClient->getDocumentDownloadTicket($documentId, DocumentTicketType::ORIGINAL);

        // 3. Get the document by passing its Id and the version type.
        // Be sure to select the exact DocumentDownloadTypes to download the type of document you want.
        // Original: the file uploaded when the document was created. It is always available.
        $original = $this->signerClient->getDocumentContent($documentId, DocumentDownloadTypes::ORIGINAL);
        $this->saveFile("./resources/downloaded-original.pdf", $original);

        // 4. You can also get the bytes directly, encoded in Base64, for a specific version type of the document
        $documentVersionBytes = new DocumentsDocumentContentModel($this->signerClient->GetDocumentContentBytesAsync($documentId, DocumentDownloadTypes::ORIGINAL));
        $this->saveFile("./resources/downloaded-original-bytes.pdf", base64_decode($documentVersionBytes["bytes"]));

        // 5. For a document that nobody has signed yet, Signatures and PrinterFriendlyVersion fail with
        //    422 and the code DocumentHasNoSignatures. getDocumentContent does not throw on HTTP errors:
        //    it returns the JSON error body, which would be saved as if it were the file. Read the document first.
        $details = new DocumentsDocumentModel($this->signerClient->getDocumentDetails($documentId));
        if (!$details->getHasSignature()) {
            echo "Document " . $documentId . " has no signatures yet, so only its Original version was downloaded.\n";
            return;
        }

        // 6. Signatures: the signed file. For a PDF signed with PAdES (the default) it is the PDF with the signatures
        //    embedded; for a CAdES signature it is a .p7s file. It holds the signatures collected so far: the document
        //    is final only when its status is Concluded. Canceled, refused and expired documents download only as
        //    Original and PrinterFriendlyVersion.
        $status = $details->getStatus();
        if (in_array($status, array(DocumentStatus::CANCELED, DocumentStatus::REFUSED, DocumentStatus::EXPIRED))) {
            echo "Document " . $documentId . " is " . $status . ", so its signed file cannot be downloaded.\n";
        } else {
            $signed = $this->signerClient->getDocumentContent($documentId, DocumentDownloadTypes::SIGNATURES);
            $this->saveFile("./resources/downloaded-signed.pdf", $signed);
        }

        // 7. PrinterFriendlyVersion: a PDF with the signature marks and a signatures manifest appended.
        $printerFriendly = $this->signerClient->getDocumentContent($documentId, DocumentDownloadTypes::PRINTER_FRIENDLY_VERSION);
        $this->saveFile("./resources/downloaded-printer-friendly.pdf", $printerFriendly);
    }

    private function saveFile($path, $content)
    {
        file_put_contents($path, $content);
        echo "Saved " . realpath($path) . "\n";
    }

}
