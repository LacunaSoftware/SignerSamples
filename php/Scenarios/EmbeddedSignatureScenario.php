<?php


namespace Lacuna\Scenarios;


use Lacuna\Signer\Model\DocumentsActionUrlRequest;
use Lacuna\Signer\Model\DocumentsActionUrlResponse;
use Lacuna\Signer\Model\DocumentsCreateDocumentRequest;
use Lacuna\Signer\Model\FlowActionsFlowActionCreateModel;
use Lacuna\Signer\Model\FlowActionType;
use Lacuna\Signer\Model\UsersParticipantUserModel;
use Lacuna\Signer\PhpClient\Builders\FileUploadBuilder;
use Lacuna\Signer\PhpClient\Models\UploadModel;

class EmbeddedSignatureScenario extends Scenario
{
    /**
     * This scenario demonstrates the creation of a document
     * and the generation of an action URL for the embedded signature
     * integration.
     */
    function run()
    {
        // 1. The file's bytes must be read by the application and uploaded
        $filePath = "resources/sample.pdf";
        $fileName = basename($filePath);
        $file = fopen($filePath, "r");
        $uploadModel = new UploadModel($this->signerClient->uploadFile($fileName, $file, "application/pdf"));
        fclose($file);

        // 2. Define the name of the document which will be visible in the application
        $fileUploadModelBuilder = new FileUploadBuilder($uploadModel);
        $fileUploadModelBuilder->setDisplayName("Embedded Signature Sample");

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


        // 5. Send the document create request
        $documentRequest = new DocumentsCreateDocumentRequest();
        $documentRequest->setFiles(
            array($fileUploadModelBuilder->toModel())
        );
        $documentRequest->setFlowActions(
            array($flowActionCreateModel)
        );

        $docResult = $this->signerClient->createDocument($documentRequest)[0];

        // 6. Get the embed URL for the participant
        $actionUrlRequest = new DocumentsActionUrlRequest();
        $actionUrlRequest->setIdentifier($user->getIdentifier());
        $actionUrlResponse = new DocumentsActionUrlResponse($this->signerClient->getActionUrl($docResult->getDocumentId(), $actionUrlRequest));


        // 7. Load the embed URL in your own application using the LacunaSignerWidget as described in
        //    https://docs.lacunasoftware.com/pt-br/articles/signer/embedded-signature.html
        echo ($actionUrlResponse->getEmbedUrl());

    }

}