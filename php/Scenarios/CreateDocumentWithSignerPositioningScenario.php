<?php

namespace Lacuna\Scenarios;

use Lacuna\Signer\PhpClient\Models\UploadModel;

class CreateDocumentWithSignerPositioningScenario extends Scenario
{
    /**
     * This scenario sets, for one document, two settings that otherwise come from the organization:
     *
     * - signerPositioningEnabled: whether the signer may place their own signature on the page.
     * - automaticSignaturePositioning: what Signer does with a signature that nobody placed: Footnote
     *   puts it in the page footer, SignatureSheet on a signature sheet at the end of the PDF, and
     *   Disabled leaves it off. Disabled can't be combined with signerPositioningEnabled = false.
     *
     * It sets them when it creates the document, then changes them with
     * PUT /api/documents/{id}/signer-positioning, which works until a participant signs or approves. A
     * null value takes the organization's current setting, and the document keeps it even if the
     * organization changes later.
     *
     * lacuna/signer-client has neither setting, so the scenario calls the REST API directly
     * (callSignerApi, in Scenario.php).
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

        // 2. Create the document: the signer places their signature, and a signature nobody placed goes
        //    on a signature sheet.
        $results = $this->callSignerApi("POST", "/api/documents", [
            'files' => [
                [
                    'id' => $uploadModel->getId(),
                    'name' => $uploadModel->getName(),
                    'contentType' => $uploadModel->getContentType(),
                    'displayName' => "Signer Positioning Sample",
                ],
            ],
            'flowActions' => [
                [
                    'type' => "Signer",
                    'user' => ['name' => "Jack Bauer", 'email' => "jack.bauer@mailinator.com", 'identifier' => "75502846369"],
                ],
            ],
            'signerPositioningEnabled' => true,
            'automaticSignaturePositioning' => "SignatureSheet",
        ]);
        $documentId = $results[0]['documentId'];
        $this->printSettings($documentId);

        // 3. Before the first signature, change them: the signer can't move the signature any more, and
        //    an unplaced one goes in a footnote.
        $this->callSignerApi("PUT", "/api/documents/$documentId/signer-positioning", [
            'signerPositioningEnabled' => false,
            'automaticSignaturePositioning' => "Footnote",
        ]);
        $this->printSettings($documentId);
    }

    private function printSettings($documentId)
    {
        $document = $this->callSignerApi("GET", "/api/documents/$documentId");
        echo "Document $documentId: signerPositioningEnabled = " . var_export(self::requiredField($document, 'signerPositioningEnabled'), true)
            . ", automaticSignaturePositioning = " . self::requiredField($document, 'automaticSignaturePositioning') . "\n";
    }
}
