<?php

// Router script of the fake Signer that FakeSigner serves with php -S. PHP keeps nothing in memory
// between requests, so the state lives in the folder in FAKE_SIGNER_DIR:
//
// - requests.jsonl: every request received, one JSON object per line (written here).
// - document-status: the status of every document that GET /api/documents/{id} returns.
// - document-details-error: a status code that GET /api/documents/{id} answers instead.
// - hold-document-details: while this file exists, GET /api/documents/{id} waits (504 after 10 s).
// - answer.json: { "status": ..., "body": "..." }, the answer to every other request (else 404).

$dir = getenv('FAKE_SIGNER_DIR');
$path = parse_url($_SERVER['REQUEST_URI'], PHP_URL_PATH);
$request = [
    'method' => $_SERVER['REQUEST_METHOD'],
    'path' => $path,
    'apiKey' => isset($_SERVER['HTTP_X_API_KEY']) ? $_SERVER['HTTP_X_API_KEY'] : null,
    'contentType' => isset($_SERVER['CONTENT_TYPE']) ? $_SERVER['CONTENT_TYPE'] : null,
    'body' => file_get_contents('php://input'),
];
file_put_contents("$dir/requests.jsonl", json_encode($request) . "\n", FILE_APPEND | LOCK_EX);

header('Content-Type: application/json');
if ($_SERVER['REQUEST_METHOD'] === 'GET' && preg_match('#^/api/documents/([0-9a-f-]{36})$#', $path, $match)) {
    $deadline = microtime(true) + 10;
    while (file_exists("$dir/hold-document-details")) {
        if (microtime(true) > $deadline) {
            // The test never released it: fail the read, so that the endpoint logs an error.
            http_response_code(504);
            echo json_encode(['message' => 'The fake Signer held this request for over 10 seconds']);
            return true;
        }
        usleep(20000);
    }
    if (file_exists("$dir/document-details-error")) {
        http_response_code((int)file_get_contents("$dir/document-details-error"));
        echo json_encode(['message' => 'The fake Signer was told to fail this request']);
        return true;
    }
    $status = file_exists("$dir/document-status") ? file_get_contents("$dir/document-status") : 'Pending';
    echo json_encode(['id' => $match[1], 'name' => 'sample.pdf', 'status' => $status]);
    return true;
}

$answer = file_exists("$dir/answer.json") ? json_decode(file_get_contents("$dir/answer.json"), true) : ['status' => 404, 'body' => ''];
http_response_code($answer['status']);
echo $answer['body'];
return true;
