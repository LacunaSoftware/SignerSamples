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

    public  function  Init(){
        $this->endPoint = "https://signer-lac.azurewebsites.net";
        $this->apiKey = "API Sample App|43fc0da834e48b4b840fd6e8c37196cf29f919e5daedba0f1a5ec17406c13a99";
        $this->signerClient = new SignerClient( $this->endPoint, $this->apiKey);
    }



    abstract protected function Run();

    protected function createDocument(){
        $filePath = "sample.pdf";
        $fileName = basename($filePath);

        $file = fopen($filePath, "r");

        $uploadModel = new UploadModel($this->signerClient->uploadFile($fileName, $file, "application/pdf" ));


        $fileUploadModelBuilder = new FileUploadBuilder($uploadModel);
        $fileUploadModelBuilder->setDisplayName("One Signer Sample");

        $user = new UsersParticipantUserModel();
        $user->setName("Jack Bauer");
        $user->setEmail("jack.bauer@mailinator.com");
        $user->setIdentifier("75502846369");
        
        $flowActionCreateModel = new FlowActionsFlowActionCreateModel();
        $flowActionCreateModel->setType(FlowActionType::SIGNER);
        $flowActionCreateModel->setUser($user);

        $documentRequest = new DocumentsCreateDocumentRequest();


        $documentRequest->setFiles(array($fileUploadModelBuilder->toModel()));


        $documentRequest->setFlowActions(array($flowActionCreateModel));



        return new DocumentsCreateDocumentResult($this->signerClient->createDocument($documentRequest));


    }

}