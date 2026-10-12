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

    protected function createDocument()
    {
        $filePath = "resources/sample.pdf";
        $fileName = basename($filePath);

        $file = fopen($filePath, "r");
        $uploadModel = new UploadModel($this->signerClient->uploadFile($fileName, $file, "application/pdf"));
        fclose($file);

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