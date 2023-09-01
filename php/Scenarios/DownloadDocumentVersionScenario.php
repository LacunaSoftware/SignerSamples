<?php


namespace Lacuna\Scenarios;


use Lacuna\Signer\Model\DocumentDownloadTypes;
use Lacuna\Signer\Model\DocumentsCreateDocumentResult;
use Lacuna\Signer\Model\DocumentsDocumentContentModel;
use Lacuna\Signer\Model\DocumentTicketType;

class DownloadDocumentVersionScenario extends Scenario
{
    /**
     * This scenario describes how to download documents.
     */
    function run()
    {
        // 1. Get a document Id
        $result = $this->createDocument();

        //2. You can get a ticket to a specific version of the document. The ticket is a temporary URL that allows you to download that version.
        $ticketDownload = $this->signerClient->getDocumentDownloadTicket($result->getDocumentId(), DocumentTicketType::ORIGINAL);

        // 3. Get the document by passing it's Id and the Ticket type
        // Be sure to select the exact DocumentTicketType to download the type of document you want.
        // Check the available types by inspecting DocumentTicketType's ENUM.
        $documentVersion = $this->signerClient->getDocumentContent($result->getDocumentId(), DocumentDownloadTypes::ORIGINAL);
        file_put_contents("./resources/documentVersion.pdf", $documentVersion);

        // 4. You can also get the bytes directly instead of a Stream for a specific version type of the document
        $documentVersionBytes = new DocumentsDocumentContentModel($this->signerClient->GetDocumentContentBytesAsync($result->getDocumentId(), DocumentDownloadTypes::ORIGINAL));
        file_put_contents("./resources/documentVersionBytes.pdf", base64_decode($documentVersionBytes["bytes"]));

    }

}