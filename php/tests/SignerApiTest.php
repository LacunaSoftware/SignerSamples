<?php

namespace Lacuna\Tests;

use Lacuna\Scenarios\Scenario;
use PHPUnit\Framework\TestCase;

// Exposes the base class's helpers to the tests.
class ApiCallScenario extends Scenario
{
    protected function run()
    {
    }

    public function call($method, $path, $body = null)
    {
        return $this->callSignerApi($method, $path, $body);
    }

    public static function required($parent, $name)
    {
        return self::requiredField($parent, $name);
    }
}

/**
 * callSignerApi and requiredField, from Scenarios/Scenario.php, against a fake Signer.
 */
class SignerApiTest extends TestCase
{
    const API_KEY = 'PHP Tests|0123456789abcdef';

    private static $signer;
    private $scenario;

    public static function setUpBeforeClass(): void
    {
        self::$signer = new FakeSigner();
    }

    public static function tearDownAfterClass(): void
    {
        self::$signer->stop();
        putenv('SIGNER_ENDPOINT');
        putenv('SIGNER_API_KEY');
    }

    protected function setUp(): void
    {
        self::$signer->clearRequests();
        // With a trailing slash, which callSignerApi drops.
        putenv('SIGNER_ENDPOINT=' . self::$signer->url . '/');
        putenv('SIGNER_API_KEY=' . self::API_KEY);
        $this->scenario = new ApiCallScenario();
        $this->scenario->init();
    }

    public function testSendsTheBodyAsJsonWithTheApiKeyAndReturnsTheDecodedAnswer()
    {
        self::$signer->setAnswer(200, '{"disableRefusal":true}');

        $result = $this->scenario->call('POST', '/api/documents', ['description' => 'ação']);

        $this->assertSame(['disableRefusal' => true], $result);
        $requests = self::$signer->requests('POST', '/api/documents');
        $this->assertCount(1, $requests);
        $this->assertSame(self::API_KEY, $requests[0]['apiKey']);
        $this->assertStringStartsWith('application/json', $requests[0]['contentType']);
        $this->assertSame(['description' => 'ação'], json_decode($requests[0]['body'], true));
    }

    public function testSendsNoBodyWhenGivenNoneAndReturnsNullForAnEmptyAnswer()
    {
        self::$signer->setAnswer(200, '');
        $path = '/api/documents/3fa85f64-5717-4562-b3fc-2c963f66afa6/resume';

        $result = $this->scenario->call('POST', $path);

        $this->assertNull($result);
        $this->assertSame('', self::$signer->requests('POST', $path)[0]['body']);
    }

    public function testThrowsWithTheStatusAndBodyOfAnErrorAnswer()
    {
        $body = '{"code":"DocumentNotPending","message":"The document is not pending"}';
        self::$signer->setAnswer(422, $body);
        $path = '/api/documents/3fa85f64-5717-4562-b3fc-2c963f66afa6/pause';

        $error = $this->callAndCatch('POST', $path, []);

        $this->assertSame("POST $path answered 422: $body", $error->getMessage());
        // The fake answers every path alike, and the message is built from the arguments: check the
        // request itself.
        $this->assertCount(1, self::$signer->requests('POST', $path));
    }

    public function testThrowsOnASuccessAnswerThatIsNotJson()
    {
        self::$signer->setAnswer(200, '%PDF-1.4 fake');
        $path = '/api/documents/3fa85f64-5717-4562-b3fc-2c963f66afa6/content';

        $error = $this->callAndCatch('GET', $path);

        $this->assertSame("GET $path answered 200 with invalid JSON: %PDF-1.4 fake", $error->getMessage());
        // A GET, with no body and so no Content-Type.
        $requests = self::$signer->requests('GET', $path);
        $this->assertCount(1, $requests);
        $this->assertNull($requests[0]['contentType']);
        $this->assertSame('', $requests[0]['body']);
    }

    public function testRequiredFieldReturnsTheFieldEvenWhenItIsFalseOrZero()
    {
        $this->assertFalse(ApiCallScenario::required(['disableRefusal' => false], 'disableRefusal'));
        $this->assertSame(0, ApiCallScenario::required(['step' => 0], 'step'));
    }

    public function testRequiredFieldThrowsWhenSignerLeftTheFieldOut()
    {
        $this->expectException(\RuntimeException::class);
        $this->expectExceptionMessage('Signer did not return timestampEnabled: this instance may predate the feature');

        ApiCallScenario::required([], 'timestampEnabled');
    }

    public function testRequiredFieldThrowsWhenSignerSentNull()
    {
        $this->expectException(\RuntimeException::class);
        $this->expectExceptionMessage('Signer did not return timestampEnabled: this instance may predate the feature');

        ApiCallScenario::required(['timestampEnabled' => null], 'timestampEnabled');
    }

    private function callAndCatch($method, $path, $body = null)
    {
        try {
            $this->scenario->call($method, $path, $body);
        } catch (\RuntimeException $e) {
            return $e;
        }
        $this->fail("$method $path did not throw");
    }
}
