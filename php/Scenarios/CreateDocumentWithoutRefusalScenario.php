<?php

namespace Lacuna\Scenarios;

use Lacuna\Signer\PhpClient\Models\UploadModel;

class CreateDocumentWithoutRefusalScenario extends Scenario
{
    /**
     * This scenario creates a document that one of its two signers cannot refuse. Signer hides the
     * refuse option from that participant, on the signing and approval pages, and rejects a refusal
     * from them. The setting is per participant: the other signer can still refuse, and your
     * application can still refuse the document on the organization's behalf (RefuseDocumentScenario).
     *
     * lacuna/signer-client 1.4.10 has no disableRefusal, so the scenario creates the document with a
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

        // 2. Create the document. Set disableRefusal on each flow action whose participant must not
        //    refuse; it defaults to false.
        $results = $this->callSignerApi("POST", "/api/documents", [
            'files' => [
                [
                    'id' => $uploadModel->getId(),
                    'name' => $uploadModel->getName(),
                    'contentType' => $uploadModel->getContentType(),
                    'displayName' => "Without Refusal Sample",
                ],
            ],
            'flowActions' => [
                [
                    'type' => "Signer",
                    'user' => ['name' => "Jack Bauer", 'email' => "jack.bauer@mailinator.com", 'identifier' => "75502846369"],
                    'disableRefusal' => true,
                ],
                [
                    'type' => "Signer",
                    'user' => ['name' => "James Bond", 'email' => "james.bond@mailinator.com", 'identifier' => "95588148061"],
                ],
            ],
        ]);
        // One result per file.
        $documentId = $results[0]['documentId'];

        // 3. Read the document back: each flow action has disableRefusal.
        $document = $this->callSignerApi("GET", "/api/documents/$documentId");
        foreach ($document['flowActions'] as $flowAction) {
            echo $flowAction['user']['name'] . " can refuse: " . ($flowAction['disableRefusal'] ? "no" : "yes") . "\n";
        }
    }
}
