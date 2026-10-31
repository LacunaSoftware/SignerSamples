<?php

namespace Lacuna\Scenarios;

class PauseAndResumeDocumentScenario extends Scenario
{
    /**
     * This scenario pauses a pending document and then resumes it. While a document is paused, nobody
     * can sign, approve or refuse it, Signer sends no reminders and the document doesn't expire; you
     * can still cancel it. Resuming lets the flow continue where it stopped. Each step e-mails the
     * participants whose turn has come and sends a webhook event, DocumentPaused or DocumentResumed.
     *
     * lacuna/signer-client 1.4.10 has no pause or resume methods, so the scenario calls the REST API
     * directly (callSignerApi, in Scenario.php). Its DocumentStatus has no Paused value either.
     */
    function run()
    {
        // 1. You need the id of a pending document.
        $documentId = $this->createDocument()->getDocumentId();

        // 2. Pause it. The reason is optional; Signer shows it on the document as statusUpdateReason.
        $this->callSignerApi("POST", "/api/documents/$documentId/pause", [
            'reason' => "Waiting for the contract review",
        ]);

        $document = $this->callSignerApi("GET", "/api/documents/$documentId");
        echo "Document $documentId is {$document['status']}: {$document['statusUpdateReason']}\n";

        // 3. Resume it. The request has no body.
        $this->callSignerApi("POST", "/api/documents/$documentId/resume");

        $document = $this->callSignerApi("GET", "/api/documents/$documentId");
        echo "Document $documentId is {$document['status']}\n";
    }
}
