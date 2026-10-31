<?php

namespace Lacuna\Scenarios;
require __DIR__ . '/../vendor/autoload.php';

use Lacuna\Signer\Model\DocumentsCreateDocumentRequest;
use Lacuna\Signer\Model\DocumentsCreateDocumentResult;
use Lacuna\Signer\Model\FlowActionsFlowActionCreateModel;
use Lacuna\Signer\Model\FlowActionType;
use Lacuna\Signer\Model\UsersParticipantUserModel;
use Lacuna\Signer\PhpClient\SignerClient;
use Lacuna\Signer\PhpClient\Models\UploadModel;
use Lacuna\Signer\PhpClient\Builders\FileUploadBuilder;

abstract class Scenario
{
    protected $signerClient;
    protected $endPoint;
    protected $apiKey;

    public function __construct()
    {

    }

    public function init()
    {
        // Signer instance URL. Without SIGNER_ENDPOINT, the samples use Lacuna's Signer sandbox.
        $this->endPoint = self::getEnvOrDefault("SIGNER_ENDPOINT", "https://signer-lac.azurewebsites.net/");
        // Application credentials token, "<application name>|<key>". Without SIGNER_API_KEY, the
        // samples use a shared key of the sandbox.
        $this->apiKey = self::getEnvOrDefault("SIGNER_API_KEY", "API Sample App|43fc0da834e48b4b840fd6e8c37196cf29f919e5daedba0f1a5ec17406c13a99");
        $this->signerClient = new SignerClient($this->endPoint, $this->apiKey);
    }

    // Returns the value of an environment variable, or the fallback when it is unset or empty.
    protected static function getEnvOrDefault($name, $fallback)
    {
        $value = getenv($name);
        return $value === false || $value === '' ? $fallback : $value;
    }

    abstract protected function run();

    // Calls the Signer REST API directly, for the features that lacuna/signer-client doesn't have yet,
    // such as pausing a document. The body is an array sent as JSON, with the field names and enum values
    // of the API reference (https://www.dropsigner.com/swagger). Returns the JSON of the response as an
    // associative array, or null when it has none. Throws on an answer other than 2xx, with the body,
    // which for a 422 holds Signer's error code and message, and on a 2xx that isn't JSON.
    protected function callSignerApi($method, $path, $body = null)
    {
        $options = [
            'headers' => ['X-Api-Key' => $this->apiKey],
            // Read the error body instead of letting Guzzle throw.
            'http_errors' => false,
            // Guzzle waits forever by default.
            'connect_timeout' => 30,
            'timeout' => 100,
        ];
        if ($body !== null) {
            $options['json'] = $body;
        }
        $response = (new \GuzzleHttp\Client())->request($method, rtrim($this->endPoint, '/') . $path, $options);

        $content = (string)$response->getBody();
        $status = $response->getStatusCode();
        if ($status < 200 || $status >= 300) {
            throw new \RuntimeException("$method $path answered $status: $content");
        }
        if ($content === '') {
            return null;
        }
        // json_decode returns null both for invalid JSON and for "null", so check the error.
        $result = json_decode($content, true);
        if (json_last_error() !== JSON_ERROR_NONE) {
            throw new \RuntimeException("$method $path answered $status with invalid JSON: $content");
        }
        return $result;
    }

    // Returns a field of a response from callSignerApi, and throws if Signer left it out or sent null: an
    // instance older than the feature ignores the request field instead of rejecting it.
    protected static function requiredField($parent, $name)
    {
        if (!isset($parent[$name])) {
            throw new \RuntimeException("Signer did not return $name: this instance may predate the feature");
        }
        return $parent[$name];
    }

    protected function createDocument()
    {
        $filePath = "resources/sample.pdf";
        $fileName = basename($filePath);

        $file = fopen($filePath, "r");
        $uploadModel = new UploadModel($this->signerClient->uploadFile($fileName, $file, "application/pdf"));
        // Guzzle closes the stream once it has sent it, and fclose() on a closed stream throws on PHP 8.
        if (is_resource($file)) {
            fclose($file);
        }

        $fileUploadModelBuilder = new FileUploadBuilder($uploadModel);
        $fileUploadModelBuilder->setDisplayName("One Signer Sample");

        // 3. For each participant on the flow, create one instance of ParticipantUserModel
        $participantUserOne = new UsersParticipantUserModel();
        $participantUserOne->setName("Jack Bauer");
        $participantUserOne->setEmail("jack.bauer@mailinator.com");
        $participantUserOne->setIdentifier("75502846369");

        $participantUserTwo = new UsersParticipantUserModel();
        $participantUserTwo->setName("James Bond");
        $participantUserTwo->setEmail("james.bond@mailinator.com");
        $participantUserTwo->setIdentifier("95588148061");

        // 4. Create a FlowActionCreateModel instance for each action (signature or approval) in the flow.
        //    This object is responsible for defining the personal data of the participant, the type of
        //    action that he will perform on the flow and the order in which this action will take place
        //    (Step property). If the Step property of all action are the same or not specified they
        //    may be executed at any time
        $flowActionCreateModelOne = new FlowActionsFlowActionCreateModel();
        $flowActionCreateModelOne->setType(FlowActionType::SIGNER);
        $flowActionCreateModelOne->setUser($participantUserOne);

        $flowActionCreateModelTwo = new FlowActionsFlowActionCreateModel();
        $flowActionCreateModelTwo->setType(FlowActionType::SIGNER);
        $flowActionCreateModelTwo->setUser($participantUserTwo);

        // 5. Send the document create request
        $documentRequest = new DocumentsCreateDocumentRequest();
        $documentRequest->setFiles(
            array($fileUploadModelBuilder->toModel())
        );
        $documentRequest->setFlowActions(
            array($flowActionCreateModelOne, $flowActionCreateModelTwo)
        );

        return $this->signerClient->createDocument($documentRequest)[0];

    }

}