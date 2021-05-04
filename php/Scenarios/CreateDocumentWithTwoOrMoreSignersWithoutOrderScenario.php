<?php


namespace Lacuna\Scenarios;


use Lacuna\Signer\Model\DocumentsCreateDocumentRequest;
use Lacuna\Signer\Model\DocumentsCreateDocumentResult;
use Lacuna\Signer\Model\FlowActionsFlowActionCreateModel;
use Lacuna\Signer\Model\FlowActionType;
use Lacuna\Signer\Model\UsersParticipantUserModel;
use Lacuna\Signer\PhpClient\Builders\FileUploadBuilder;
use Lacuna\Signer\PhpClient\Models\UploadModel;

class CreateDocumentWithTwoOrMoreSignersWithoutOrderScenario extends Scenario
{
    function run(){
        $filePath = "sample.pdf";
        $fileName = basename($filePath);

        $file = fopen($filePath, "r");

        $uploadModel = new UploadModel($this->signerClient->uploadFile($fileName, $file, "application/pdf"));


        $fileUploadModelBuilder = new FileUploadBuilder($uploadModel);
        $fileUploadModelBuilder->setDisplayName("Two Signers Without Order Sample");

        $participantUserOne = new UsersParticipantUserModel();
        $participantUserOne ->setName("Jack Bauer");
        $participantUserOne ->setEmail("jack.bauer@mailinator.com");
        $participantUserOne ->setIdentifier("05819884183");


        $flowActionCreateModelOne = new FlowActionsFlowActionCreateModel();
        $flowActionCreateModelOne->setType(FlowActionType::SIGNER);
        $flowActionCreateModelOne->setUser( $participantUserOne );

        $participantUserTwo = new UsersParticipantUserModel();
        $participantUserTwo ->setName("James Bond");
        $participantUserTwo ->setEmail("james.bond@mailinator.com");
        $participantUserTwo ->setIdentifier("95588148061");


        $flowActionCreateModelTwo = new FlowActionsFlowActionCreateModel();
        $flowActionCreateModelTwo->setType(FlowActionType::SIGNER);
        $flowActionCreateModelTwo->setUser( $participantUserTwo );


        $documentRequest = new DocumentsCreateDocumentRequest();
        $documentRequest->setFiles(array($fileUploadModelBuilder->toModel()));
        $documentRequest->setFlowActions(array($flowActionCreateModelOne, $flowActionCreateModelTwo));


        $docResult = new DocumentsCreateDocumentResult($this->signerClient->createDocument($documentRequest->__toString()));

        echo "Document " . $docResult->getDocumentId() . " created\n";
    }

}