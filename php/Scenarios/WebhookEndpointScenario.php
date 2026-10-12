<?php


namespace Lacuna\Scenarios;


use Lacuna\Signer\Model\DocumentsDocumentModel;
use Lacuna\Signer\Model\DocumentStatus;
use Lacuna\Signer\Model\WebhookTypes;

/**
 * A minimal webhook endpoint, served by PHP's built-in web server (no web framework). Start it from
 * the php folder with
 *
 *     php -S localhost:8080 webhook.php
 *
 * and it answers on http://localhost:8080/webhook. It follows the rules of
 * https://docs.lacunasoftware.com/en-us/articles/signer/webhooks#best-practices:
 *
 * 1. Check the auth header registered with the webhook: Signer does not sign the body.
 * 2. Answer 200 at once and process the event afterwards. Signer retries a non-2xx answer or a
 *    connection error (up to 10 times by default), so slow work before answering causes retries.
 * 3. Be idempotent: the same event can arrive more than once.
 * 4. Don't trust the order of events: read the document from Signer before acting on it.
 *
 * The README shows how to test it with curl, and how to receive real events through an HTTPS tunnel.
 */
class WebhookEndpointScenario extends Scenario
{
    const WEBHOOK_PATH = '/webhook';

    // The token registered with the webhook under Organization > Integration, with the auth type
    // "Bearer": Signer then sends "Authorization: Bearer <token>". Set SIGNER_WEBHOOK_TOKEN to a long
    // random value of your own; without it, the sample expects "my-webhook-token". (The port is the
    // one in the php -S command.)
    private static function expectedAuthorization()
    {
        return 'Bearer ' . self::getEnvOrDefault('SIGNER_WEBHOOK_TOKEN', 'my-webhook-token');
    }

    function run()
    {
        if (PHP_SAPI === 'cli') {
            echo "This scenario is a web endpoint. Start it with: php -S localhost:8080 webhook.php\n";
            return;
        }

        if ($_SERVER['REQUEST_METHOD'] !== 'POST' || parse_url($_SERVER['REQUEST_URI'], PHP_URL_PATH) !== self::WEBHOOK_PATH) {
            http_response_code(404);
            return;
        }

        // 1. Check the auth header before reading the body. hash_equals compares in constant time, so
        //    the response time doesn't reveal how much of the token matched.
        if (!hash_equals(self::expectedAuthorization(), $_SERVER['HTTP_AUTHORIZATION'] ?? '')) {
            http_response_code(401);
            return;
        }

        // The body is { "type": "...", "data": { ... } }, with camelCase fields.
        $body = file_get_contents('php://input');
        $webhook = json_decode($body, true);
        if (!is_array($webhook) || !is_string($webhook['type'] ?? null) || $webhook['type'] === '') {
            // Not JSON, or not shaped like a webhook.
            http_response_code(400);
            return;
        }

        // 3. Answer 200 to an event already received, so that Signer stops sending it, but don't
        //    process it again.
        $key = $this->getEventKey($webhook);
        if (!$this->recordEvent($key)) {
            $this->log("Ignored a repeated event: $key");
            http_response_code(200);
            return;
        }

        // 2. Answer now, then process the event.
        $this->answerNow(200);
        $this->log("Accepted: $key");
        try {
            $this->processEvent($webhook, $body);
        } catch (\Throwable $e) {
            // The event was already answered, so Signer won't send it again: in production, keep it in
            // your storage and retry it from there. (The client library's exceptions carry the whole
            // inner exception, with its stack trace, in the message; log its first line.)
            $this->log("Could not process $key: " . strtok($e->getMessage(), "\n"));
        }
    }

    private function processEvent($webhook, $body)
    {
        if (($webhook['type'] ?? null) === WebhookTypes::DOCUMENT_CONCLUDED) {
            // 4. Events can arrive out of order, and the payload is a notification, not the source of
            //    truth: read the document and act only if it is really concluded.
            $document = new DocumentsDocumentModel($this->signerClient->getDocumentDetails($webhook['data']['id']));
            if ($document->getStatus() !== DocumentStatus::CONCLUDED) {
                $this->log("Document " . $document->getId() . " is " . $document->getStatus() . ", not Concluded: nothing to do yet");
                return;
            }
        }

        // The switch on the event type from CheckDocumentStatusScenario. It echoes its messages, and the
        // response is already sent, so send them to the server's console instead.
        ob_start();
        try {
            (new CheckDocumentStatusScenario())->HandleWebhook($body);
        } finally {
            $output = ob_get_clean();
        }
        if ($output !== '') {
            $this->log($output);
        }
    }

    // The key of an event: its type, the document id and, for an event about one participant's action,
    // the flow action id (a document gets one DocumentSigned per signer).
    private function getEventKey($webhook)
    {
        $data = is_array($webhook['data'] ?? null) ? $webhook['data'] : [];
        // DocumentsCreated and DocumentDeleted carry a list of documents instead of one id.
        $documents = is_array($data['documents'] ?? null) ? $data['documents'] : [];
        $documentId = $data['id'] ?? implode(',', array_column($documents, 'id'));
        $flowActionId = $data['signature']['flowActionId']
            ?? $data['approval']['flowActionId']
            ?? $data['refusal']['flowActionId']
            ?? null;
        return implode(':', array_filter([$webhook['type'], $documentId, $flowActionId]));
    }

    // Records the key of an event, and returns false if it was already recorded. PHP keeps nothing in
    // memory between requests, so this sample creates one empty file per key in the temp folder: fopen
    // mode 'x' fails if the file exists, which checks and records the key in one atomic step.
    // Production needs durable storage, for example a table with a unique index on the key, shared by
    // every instance of the app.
    private function recordEvent($key)
    {
        $folder = sys_get_temp_dir() . '/signer-webhook-events';
        if (!is_dir($folder) && !@mkdir($folder, 0700, true) && !is_dir($folder)) {
            throw new \RuntimeException("Cannot create $folder");
        }
        $path = $folder . '/' . hash('sha256', $key);
        $file = @fopen($path, 'x');
        if ($file === false) {
            if (file_exists($path)) {
                return false;
            }
            throw new \RuntimeException("Cannot create $path");
        }
        fclose($file);
        return true;
    }

    // Sends the status code and ends the response, so that the script can go on working after the
    // client has its answer.
    private function answerNow($statusCode)
    {
        ignore_user_abort(true);
        http_response_code($statusCode);
        header('Content-Length: 0');
        header('Connection: close');
        if (function_exists('fastcgi_finish_request')) {
            fastcgi_finish_request(); // PHP-FPM
        } else {
            // Other servers, such as PHP's built-in one: send the headers now.
            while (ob_get_level() > 0) {
                ob_end_flush();
            }
            flush();
        }
    }

    // Writes to the server's console (or log), not to the response.
    private function log($message)
    {
        error_log($message);
    }
}
