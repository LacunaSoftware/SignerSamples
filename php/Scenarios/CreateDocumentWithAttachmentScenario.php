<?php


namespace Lacuna\Scenarios;


use Lacuna\Signer\Model\AttachmentsAttachmentUploadModel;
use Lacuna\Signer\Model\DocumentsCreateDocumentRequest;
use Lacuna\Signer\Model\DocumentsCreateDocumentResult;
use Lacuna\Signer\Model\FlowActionsFlowActionCreateModel;
use Lacuna\Signer\Model\FlowActionType;
use Lacuna\Signer\Model\UsersParticipantUserModel;
use Lacuna\Signer\PhpClient\Builders\AttachmentUploadBuilder;
use Lacuna\Signer\PhpClient\Builders\FileUploadBuilder;
use Lacuna\Signer\PhpClient\Models\UploadModel;

class CreateDocumentWithAttachmentScenario extends Scenario
{

    /**
     * This scenario demonstrates the creation of a document with Attachment.
     */
    function run()
    {   // 1. The file's bytes must be read by the application and uploaded
        $filePath = "resources/sample.pdf";
        $fileName = basename($filePath);
        $file = fopen($filePath, "r");
        $uploadModel = new UploadModel($this->signerClient->uploadFile($fileName, $file, "application/pdf"));
        fclose($file);

        // 2. Define the name of the document which will be visible in the application
        $fileUploadModelBuilder = new FileUploadBuilder($uploadModel);
        $fileUploadModelBuilder->setDisplayName("One Signer Sample");

        // Repeat the same steps above but now for the attachment
        $attachmentPath = "resources/sample.pdf";
        $attachmentName = basename($filePath);
        $attachment = fopen($attachmentPath, "r");
        $attachmentUpload = new UploadModel($this->signerClient->uploadFile($attachmentName, $attachment, "application/pdf"));

        // 3. Define the name of the document which will be visible in the application
        $attachmentUploadModel = new AttachmentUploadBuilder($attachmentUpload);
        $attachmentUploadModel->setDisplayName("One Attachment Sample");


        // 4. For each participant on the flow, create one instance of ParticipantUserModel
        $user = new UsersParticipantUserModel();
        $user->setName("Jack Bauer");
        $user->setEmail("jack.bauer@mailinator.com");
        $user->setIdentifier("75502846369");


        // 5. Create a FlowActionCreateModel instance for each action (signature or approval) in the flow.
        //    This object is responsible for defining the personal data of the participant and the type of
        //    action that he will perform on the flow
        $flowActionCreateModel = new FlowActionsFlowActionCreateModel();
        $flowActionCreateModel->setType(FlowActionType::APPROVER);
        $flowActionCreateModel->setUser($user);

        // 6. Send the document create request with whith the attachment attribute
        $documentRequest = new DocumentsCreateDocumentRequest();
        $documentRequest->setFiles(
            array($fileUploadModelBuilder->toModel())
        );
        $documentRequest->setAttachments(
            array($attachmentUploadModel->toModel())
        );
        $documentRequest->setFlowActions(
            array($flowActionCreateModel)
        );

        // 7. Result
        $docResult = $this->signerClient->createDocument($documentRequest)[0];

        echo "Document " . $docResult->getDocumentId() . " created\n";

    }
}