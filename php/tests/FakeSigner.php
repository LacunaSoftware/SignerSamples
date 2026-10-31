<?php

namespace Lacuna\Tests;

/**
 * A fake Signer on localhost, for the tests: tests/fake-signer.php served by php -S. It records
 * every request and answers GET /api/documents/{id} with a document whose status is the one given
 * to setDocumentStatus. setAnswer sets what every other request gets.
 */
class FakeSigner
{
    private $server;
    private $dir;
    public $url;

    public function __construct()
    {
        $this->dir = sys_get_temp_dir() . '/fake-signer-' . bin2hex(random_bytes(8));
        mkdir($this->dir);
        $this->server = new PhpServer(__DIR__, 'fake-signer.php', ['FAKE_SIGNER_DIR' => $this->dir]);
        $this->url = $this->server->url;
    }

    // The requests received, each an array with method, path, apiKey, contentType and body; with a
    // method and path, only those to them.
    public function requests($method = null, $path = null)
    {
        $file = "$this->dir/requests.jsonl";
        $lines = file_exists($file) ? file($file, FILE_IGNORE_NEW_LINES | FILE_SKIP_EMPTY_LINES) : [];
        $requests = array_map(function ($line) {
            return json_decode($line, true);
        }, $lines);
        return array_values(array_filter($requests, function ($request) use ($method, $path) {
            return ($method === null || $request['method'] === $method) && ($path === null || $request['path'] === $path);
        }));
    }

    public function clearRequests()
    {
        @unlink("$this->dir/requests.jsonl");
    }

    public function setAnswer($status, $body)
    {
        file_put_contents("$this->dir/answer.json", json_encode(['status' => $status, 'body' => $body]));
    }

    public function setDocumentStatus($status)
    {
        file_put_contents("$this->dir/document-status", $status);
    }

    // While held, GET /api/documents/{id} waits (up to 10 seconds).
    public function holdDocumentDetails($hold)
    {
        if ($hold) {
            touch("$this->dir/hold-document-details");
        } else {
            @unlink("$this->dir/hold-document-details");
        }
    }

    public function stop()
    {
        $this->server->stop();
        array_map('unlink', glob("$this->dir/*"));
        rmdir($this->dir);
    }
}
