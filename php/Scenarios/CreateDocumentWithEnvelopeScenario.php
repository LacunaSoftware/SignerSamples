<?php


namespace Lacuna\Scenarios;


use Lacuna\Signer\Model\DocumentsCreateDocumentRequest;
use Lacuna\Signer\Model\FlowActionsFlowActionCreateModel;
use Lacuna\Signer\Model\FlowActionType;
use Lacuna\Signer\Model\UsersParticipantUserModel;
use Lacuna\Signer\PhpClient\Builders\FileUploadBuilder;
use Lacuna\Signer\PhpClient\Models\UploadModel;

class CreateDocumentWithEnvelopeScenario extends Scenario
{
    /**
     * This scenario demonstrates the creation of a document with envelope.
     */
    function run()
    {
        // 1. The file's bytes must be read by the application and uploaded
        $filePath = "resources/sample.pdf";
        $fileName = basename($filePath);
        $file = fopen($filePath, "r");
        $uploadModel = new UploadModel($this->signerClient->uploadFile($fileName, $file, "application/pdf"));
        fclose($file);

        $file = fopen($filePath, "r");
        $uploadModelTwo = new UploadModel($this->signerClient->uploadFile($fileName, $file, "application/pdf"));
        fclose($file);

        // 2. Define the name of the document which will be visible in the application
        $fileUploadModelBuilder = new FileUploadBuilder($uploadModel);
        $fileUploadModelBuilder->setDisplayName("One Envelope Sample");

        // 2. Define the name of the document which will be visible in the application
        $fileUploadModelBuilderTwo = new FileUploadBuilder($uploadModelTwo);
        $fileUploadModelBuilderTwo->setDisplayName("One Envelope Sample");


        // 3. For each participant on the flow, create one instance of ParticipantUserModel
        $user = new UsersParticipantUserModel();
        $user->setName("Jack Bauer");
        $user->setEmail("jack.bauer@mailinator.com");
        $user->setIdentifier("75502846369");


        // 4. Create a FlowActionCreateModel instance for each action (signature or approval) in the flow.
        //    This object is responsible for defining the personal data of the participant and the type of
        //    action that he will perform on the flow
        $flowActionCreateModel = new FlowActionsFlowActionCreateModel();
        $flowActionCreateModel->setType(FlowActionType::SIGNER);
        $flowActionCreateModel->setUser($user);

        // 5. Send the document create request setting up the attribute "isEnvelope" as "true" and you MUST give a name to this envelope "envelopeName"
        $documentRequest = new DocumentsCreateDocumentRequest();
        $documentRequest->setEnvelopeName("Envelope Sample");
        $documentRequest->setIsEnvelope(true);

        $documentRequest->setFiles(
            array($fileUploadModelBuilder->toModel(), $fileUploadModelBuilderTwo->toModel())
        );
        $documentRequest->setFlowActions(
            array($flowActionCreateModel)
        );


        $docResult = $this->signerClient->createDocument($documentRequest)[0];

        echo "Document " . $docResult->getDocumentId() . " created\n";

    }

}