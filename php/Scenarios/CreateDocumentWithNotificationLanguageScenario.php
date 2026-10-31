<?php

namespace Lacuna\Scenarios;

use Lacuna\Signer\PhpClient\Models\UploadModel;

class CreateDocumentWithNotificationLanguageScenario extends Scenario
{
    /**
     * This scenario chooses the language of each e-mail that Signer sends about a document:
     *
     * - notificationLanguage on a flow action: the e-mails to that participant.
     * - notifiedEmailsData on the document: the addresses told when the document is concluded, each
     *   with its own language. It replaces the older notifiedEmails list of addresses.
     *
     * The languages are Pt, Es and En; without one, Signer uses the instance's default language.
     *
     * lacuna/signer-client has neither field, so the scenario creates the document with a direct
     * call to the REST API (callSignerApi, in Scenario.php).
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

        // 2. Create the document: one signer is notified in English, the other in Spanish, and a third
        //    address learns in Portuguese that the document is concluded.
        $results = $this->callSignerApi("POST", "/api/documents", [
            'files' => [
                [
                    'id' => $uploadModel->getId(),
                    'name' => $uploadModel->getName(),
                    'contentType' => $uploadModel->getContentType(),
                    'displayName' => "Notification Language Sample",
                ],
            ],
            'flowActions' => [
                [
                    'type' => "Signer",
                    'user' => ['name' => "Jack Bauer", 'email' => "jack.bauer@mailinator.com", 'identifier' => "75502846369"],
                    'notificationLanguage' => "En",
                ],
                [
                    'type' => "Signer",
                    'user' => ['name' => "James Bond", 'email' => "james.bond@mailinator.com", 'identifier' => "95588148061"],
                    'notificationLanguage' => "Es",
                ],
            ],
            'notifiedEmailsData' => [
                ['email' => "jill.palmer@mailinator.com", 'notificationLanguage' => "Pt"],
            ],
        ]);
        $documentId = $results[0]['documentId'];

        // 3. Read the document back.
        $document = $this->callSignerApi("GET", "/api/documents/$documentId");
        foreach ($document['flowActions'] as $flowAction) {
            echo "{$flowAction['user']['email']} is notified in " . self::requiredField($flowAction, 'notificationLanguage') . "\n";
        }
        foreach (self::requiredField($document, 'notifiedEmailsData') as $notifiedEmail) {
            echo "{$notifiedEmail['email']} learns of the conclusion in " . self::requiredField($notifiedEmail, 'notificationLanguage') . "\n";
        }
    }
}
