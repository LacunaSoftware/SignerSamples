<?php

namespace Lacuna\Scenarios;

use Lacuna\Signer\PhpClient\Models\UploadModel;

class CreatePdfADocumentWithTimestampScenario extends Scenario
{
    /**
     * This scenario creates a document that Signer converts to PDF/A, for long-term archiving, and
     * whose signatures get a timestamp:
     *
     * - convertToPdfA and pdfAProfile: Signer converts the uploaded PDF before the flow starts. The
     *   result then has a new uploadId, not the one you sent: use documentId.
     * - timestampEnabled: overrides the organization's timestamp setting for this document. true
     *   timestamps every signature and false none; leave it out to use the organization's setting. On
     *   an instance without a timestamper, Signer rejects a document that sets it.
     *
     * lacuna/signer-client has none of these fields, so the scenario creates the document with a
     * direct call to the REST API (callSignerApi, in Scenario.php).
     */
    function run()
    {
        // 1. Upload the file with the client library, as in the other scenarios.
        $filePath = "resources/sample.pdf";
        $file = fopen($filePath, "r");
        $uploadModel = new UploadModel($this->signerClient->uploadFile(basename($filePath), $file, "application/pdf"));
        // Guzzle closes the stream once it has sent it, and fclose() on a closed stream throws on PHP 8.
        if (is_resource($file)) {
            fclose($file);
        }

        // 2. Create the document. The profiles go from PdfA1a to PdfA4f; PdfA2b is a common choice for
        //    archiving.
        $results = $this->callSignerApi("POST", "/api/documents", [
            'files' => [
                [
                    'id' => $uploadModel->getId(),
                    'name' => $uploadModel->getName(),
                    'contentType' => $uploadModel->getContentType(),
                    'displayName' => "PDF/A With Timestamp Sample",
                ],
            ],
            'flowActions' => [
                [
                    'type' => "Signer",
                    'user' => ['name' => "Jack Bauer", 'email' => "jack.bauer@mailinator.com", 'identifier' => "75502846369"],
                ],
            ],
            'convertToPdfA' => true,
            'pdfAProfile' => "PdfA2b",
            'timestampEnabled' => true,
        ]);
        $documentId = $results[0]['documentId'];

        // 3. Read the document back.
        $document = $this->callSignerApi("GET", "/api/documents/$documentId");
        echo "Document $documentId: timestampEnabled = " . var_export(self::requiredField($document, 'timestampEnabled'), true) . "\n";
    }
}
