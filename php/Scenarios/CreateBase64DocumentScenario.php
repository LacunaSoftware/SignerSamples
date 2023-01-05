<?php


namespace Lacuna\Scenarios;

use Lacuna\Signer\Model\DocumentsCreateDocumentRequest;
use Lacuna\Signer\Model\FileUploadModel;
use Lacuna\Signer\Model\FlowActionsFlowActionCreateModel;
use Lacuna\Signer\Model\FlowActionType;
use Lacuna\Signer\Model\UploadsUploadBytesModel;
use Lacuna\Signer\Model\UploadsUploadBytesRequest;
use Lacuna\Signer\Model\UsersParticipantUserModel;
use Lacuna\Signer\PhpClient\Builders\FileUploadBuilder;
use Lacuna\Signer\PhpClient\Models\UploadModel;
use Lacuna\Signer\PhpClient\Params\PaginatedSearchParams;
use stdClass;

class CreateBase64DocumentScenario extends Scenario
{
    /**
     * This scenario demonstrates the creation of a document into a new folder.
     */
    function run()
    {
        // 1. The file's bytes must be read by the application and uploaded
        $filePath = "resources/sample.pdf";
        $fileName = basename($filePath);
        // read file as string
        $file = file_get_contents($filePath);
        // encode to base 64
        $file = base64_encode($file);
        $uploadBytesRequest = new UploadsUploadBytesRequest(array(
            'bytes' => $file
        ));
        $uploadBytesModel = new UploadsUploadBytesModel($this->signerClient->uploadFileBytes($uploadBytesRequest));

        // create new anonymous object and then send it as parameter to the UploadModel constructor
        $upload = new stdClass;
        $upload->id = $uploadBytesModel['id'];
        $upload->name = "base 64 file";
        $upload->location = "";
        $upload->contentType = "application/pdf";
        $uploadModel = new UploadModel($upload);

        $fileUploadModelBuilder = new FileUploadBuilder($uploadModel);
        $fileUploadModelBuilder->setDisplayName("Base64 pdf file");
        
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

        echo "Document " . $docResult->getDocumentId() . " created\n";

        return $docResult;
    }
}