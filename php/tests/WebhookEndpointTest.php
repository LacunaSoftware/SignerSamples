<?php

namespace Lacuna\Tests;

use GuzzleHttp\Client;
use PHPUnit\Framework\TestCase;

/**
 * The webhook endpoint's rules: check the token, answer at once, ignore repeated events, and read
 * the document back before acting on DocumentConcluded. webhook.php is served with php -S, as the
 * README runs it, with its own fake Signer and its own temp folder for the keys of the events it
 * received; the tests read what it writes to the server's console.
 */
class WebhookEndpointTest extends TestCase
{
    const TOKEN = 'webhook-tests-token';

    private static $signer;
    private static $endpoint;
    private static $tempDir;
    private static $http;

    public static function setUpBeforeClass(): void
    {
        self::$signer = new FakeSigner();
        self::$tempDir = sys_get_temp_dir() . '/webhook-tests-' . bin2hex(random_bytes(8));
        mkdir(self::$tempDir);
        self::$endpoint = new PhpServer(dirname(__DIR__), 'webhook.php', [
            'SIGNER_ENDPOINT' => self::$signer->url,
            'SIGNER_API_KEY' => 'PHP Tests|0123456789abcdef',
            'SIGNER_WEBHOOK_TOKEN' => self::TOKEN,
            // sys_get_temp_dir() reads TMPDIR on Linux and macOS, TMP or TEMP on Windows.
            'TMPDIR' => self::$tempDir,
            'TMP' => self::$tempDir,
            'TEMP' => self::$tempDir,
        ]);
        self::$http = new Client(['base_uri' => self::$endpoint->url, 'http_errors' => false, 'timeout' => 30]);
    }

    public static function tearDownAfterClass(): void
    {
        self::$endpoint->stop();
        self::$signer->stop();
        self::removeFolder(self::$tempDir);
    }

    public function testEventWithoutTheTokenAnswers401()
    {
        $response = $this->post(self::signedEvent(self::newId(), self::newId()), null);

        $this->assertSame(401, $response->getStatusCode());
    }

    public function testEventWithAnotherTokenAnswers401()
    {
        $response = $this->post(self::signedEvent(self::newId(), self::newId()), 'not-the-token');

        $this->assertSame(401, $response->getStatusCode());
    }

    public function testGetAndOtherPathsAnswer404()
    {
        $this->assertSame(404, self::$http->get('/webhook')->getStatusCode());
        $this->assertSame(404, $this->post(self::signedEvent(self::newId(), self::newId()), self::TOKEN, '/other')->getStatusCode());
    }

    /**
     * @dataProvider notWebhooks
     */
    public function testBodyThatIsNotAWebhookAnswers400($body)
    {
        $this->assertSame(400, $this->post($body)->getStatusCode());
    }

    public static function notWebhooks()
    {
        return [
            'not json' => ['not json'],
            'no type' => ['{ "data": { "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6" } }'],
        ];
    }

    public function testSignedEventIsAcceptedThenHandled()
    {
        $documentId = self::newId();
        $flowActionId = self::newId();

        $response = $this->post(self::signedEvent($documentId, $flowActionId));

        $this->assertSame(200, $response->getStatusCode());
        $this->waitFor("Accepted: DocumentSigned:$documentId:$flowActionId");
        $this->waitFor("Document $documentId is Signed");
    }

    public function testRepeatedEventAnswers200ButIsHandledOnce()
    {
        $documentId = self::newId();
        $flowActionId = self::newId();

        $this->post(self::signedEvent($documentId, $flowActionId));
        $this->waitFor("Document $documentId is Signed");
        $repeated = $this->post(self::signedEvent($documentId, $flowActionId));

        $this->assertSame(200, $repeated->getStatusCode());
        $output = $this->waitFor("Ignored a repeated event: DocumentSigned:$documentId:$flowActionId");
        $this->assertSame(1, substr_count($output, "Document $documentId is Signed"));
    }

    /**
     * The flow action id is in "signature", "approval" or "refusal", by event type; without it the
     * second participant's event would look like a repeat of the first and be dropped.
     *
     * @dataProvider participantEvents
     */
    public function testEventsOfTwoParticipantsAreTwoEvents($type, $part, $what)
    {
        $documentId = self::newId();
        $first = self::newId();
        $second = self::newId();

        $this->post(self::participantEvent($type, $part, $documentId, $first));
        $this->post(self::participantEvent($type, $part, $documentId, $second));

        $this->waitFor("Accepted: $type:$documentId:$first");
        $this->waitFor("Accepted: $type:$documentId:$second");
        $output = $this->waitFor("Document $documentId is $what", 2);
        $this->assertStringNotContainsString('Could not process', $output);
    }

    public static function participantEvents()
    {
        return [
            'signed' => ['DocumentSigned', 'signature', 'Signed'],
            'approved' => ['DocumentApproved', 'approval', 'Approved'],
            'refused' => ['DocumentRefused', 'refusal', 'Refused'],
        ];
    }

    public function testEventIsAnsweredBeforeItIsProcessed()
    {
        $documentId = self::newId();
        self::$signer->setDocumentStatus('Concluded');
        // Processing a DocumentConcluded event reads the document; hold that read until the answer arrives.
        self::$signer->holdDocumentDetails(true);
        try {
            $response = $this->post(self::concludedEvent($documentId), self::TOKEN, '/webhook', 5);

            $this->assertSame(200, $response->getStatusCode());
            $this->assertStringNotContainsString("Document $documentId is concluded", self::$endpoint->output());
        } finally {
            self::$signer->holdDocumentDetails(false);
        }
        $this->waitFor("Document $documentId is concluded");
    }

    public function testConcludedEventIsHandledAfterReadingTheDocumentAsConcluded()
    {
        $documentId = self::newId();
        self::$signer->setDocumentStatus('Concluded');

        $this->assertSame(200, $this->post(self::concludedEvent($documentId))->getStatusCode());

        $this->waitFor("Document $documentId is concluded");
        $this->assertCount(1, self::$signer->requests('GET', "/api/documents/$documentId"));
    }

    public function testConcludedEventForADocumentStillPendingDoesNothing()
    {
        $documentId = self::newId();
        self::$signer->setDocumentStatus('Pending');

        $this->assertSame(200, $this->post(self::concludedEvent($documentId))->getStatusCode());

        $output = $this->waitFor("Document $documentId is Pending, not Concluded: nothing to do yet");
        $this->assertStringNotContainsString("Document $documentId is concluded", $output);
    }

    public function testDeletedEventIsKeyedOnAllTheDocumentsItLists()
    {
        $first = self::newId();
        $second = self::newId();

        $response = $this->post(json_encode(['type' => 'DocumentDeleted', 'data' => ['documents' => [['id' => $first], ['id' => $second]]]]));

        $this->assertSame(200, $response->getStatusCode());
        $this->waitFor("Accepted: DocumentDeleted:$first,$second");
        $this->assertProcessedWithoutError();
    }

    public function testPausedEventIsAcceptedThoughHandleWebhookDoesNotHandleIt()
    {
        $documentId = self::newId();

        $response = $this->post(json_encode(['type' => 'DocumentPaused', 'data' => ['id' => $documentId, 'name' => 'sample.pdf']]));

        $this->assertSame(200, $response->getStatusCode());
        $this->waitFor("Accepted: DocumentPaused:$documentId");
        $this->assertProcessedWithoutError();
    }

    // HandleWebhook prints nothing for an event it doesn't handle. php -S serves one request at a
    // time, so once a signed event sent afterwards is handled, the earlier one is done too.
    private function assertProcessedWithoutError()
    {
        $documentId = self::newId();
        $this->post(self::signedEvent($documentId, self::newId()));
        $output = $this->waitFor("Document $documentId is Signed");
        $this->assertStringNotContainsString('Could not process', $output);
    }

    private function post($body, $token = self::TOKEN, $path = '/webhook', $timeout = 30)
    {
        $headers = ['Content-Type' => 'application/json'];
        if ($token !== null) {
            $headers['Authorization'] = "Bearer $token";
        }
        return self::$http->post($path, ['headers' => $headers, 'body' => $body, 'timeout' => $timeout]);
    }

    // Waits until the endpoint has written the text the given number of times, and returns all it wrote.
    private function waitFor($text, $times = 1)
    {
        $deadline = microtime(true) + 10;
        while (substr_count($output = self::$endpoint->output(), $text) < $times) {
            if (microtime(true) > $deadline) {
                $this->fail("The endpoint did not write \"$text\" $times time(s). It wrote:\n$output");
            }
            usleep(20000);
        }
        return $output;
    }

    private static function signedEvent($documentId, $flowActionId)
    {
        return self::participantEvent('DocumentSigned', 'signature', $documentId, $flowActionId);
    }

    // An event about one participant's action: "part" is "signature", "approval" or "refusal".
    private static function participantEvent($type, $part, $documentId, $flowActionId)
    {
        return json_encode([
            'type' => $type,
            'data' => [
                'id' => $documentId,
                'name' => 'sample.pdf',
                $part => ['flowActionId' => $flowActionId, 'name' => 'Jack Bauer', 'identifier' => '75502846369', 'emailAddress' => 'jack.bauer@mailinator.com'],
            ],
        ]);
    }

    private static function concludedEvent($documentId)
    {
        return json_encode(['type' => 'DocumentConcluded', 'data' => ['id' => $documentId, 'name' => 'sample.pdf']]);
    }

    private static function newId()
    {
        $bytes = bin2hex(random_bytes(16));
        return sprintf('%s-%s-4%s-a%s-%s', substr($bytes, 0, 8), substr($bytes, 8, 4), substr($bytes, 13, 3), substr($bytes, 17, 3), substr($bytes, 20, 12));
    }

    private static function removeFolder($folder)
    {
        foreach (glob("$folder/*") as $path) {
            is_dir($path) ? self::removeFolder($path) : unlink($path);
        }
        rmdir($folder);
    }
}
