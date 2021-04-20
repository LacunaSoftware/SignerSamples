<?php


namespace Lacuna\Scenarios;


use Lacuna\Signer\Model\DocumentsCreateDocumentRequest;
use Lacuna\Signer\Model\DocumentsCreateDocumentResult;
use Lacuna\Signer\Model\FlowActionsFlowActionCreateModel;
use Lacuna\Signer\Model\FlowActionType;
use Lacuna\Signer\Model\UsersParticipantUserModel;
use Lacuna\Signer\PhpClient\Builders\FileUploadBuilder;
use Lacuna\Signer\PhpClient\Models\UploadModel;

class CreateDocumentWithOneSignerScenario extends Scenario
{
    function run (){
        $filePath = "sample.pdf";
        $fileName = basename($filePath);

        $file = fopen($filePath, "r");

        $uploadModel = new UploadModel($this->signerClient->uploadFile($fileName, $file, "application/pdf" ));


        $fileUploadModelBuilder = new FileUploadBuilder($uploadModel);
        $fileUploadModelBuilder->setDisplayName("One Signer Sample");

        $user = new UsersParticipantUserModel();
        $user->setName("Jack Bauer");
        $user->setEmail("jack.bauer@mailinator.com");
        $user->setIdentifier("05819884183");


        $flowActionCreateModel = new FlowActionsFlowActionCreateModel();
        $flowActionCreateModel->setType(FlowActionType::SIGNER);
        $flowActionCreateModel->setUser($user);

        $documentRequest = new DocumentsCreateDocumentRequest();


        $documentRequest->setFiles(array($fileUploadModelBuilder->toModel()));


        $documentRequest->setFlowActions(array($flowActionCreateModel));



        $docResult = new DocumentsCreateDocumentResult($this->signerClient->createDocument($documentRequest->__toString()));

        echo "Document " .  $docResult->getDocumentId() ." created\n";

    }

}