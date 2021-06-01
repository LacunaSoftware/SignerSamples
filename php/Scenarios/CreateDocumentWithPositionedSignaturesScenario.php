<?php


namespace Lacuna\Scenarios;


use Lacuna\Signer\Model\DocumentMarkPrePositionedDocumentMarkModel;
use Lacuna\Signer\Model\DocumentMarkType;
use Lacuna\Signer\Model\DocumentsCreateDocumentRequest;
use Lacuna\Signer\Model\DocumentsCreateDocumentResult;
use Lacuna\Signer\Model\FlowActionsFlowActionCreateModel;
use Lacuna\Signer\Model\FlowActionType;
use Lacuna\Signer\Model\UsersParticipantUserModel;
use Lacuna\Signer\PhpClient\Builders\FileUploadBuilder;
use Lacuna\Signer\PhpClient\Models\UploadModel;

class CreateDocumentWithPositionedSignaturesScenario extends Scenario
{
    /**
     * This scenario demonstrates the creation of a document with
     * Prepositioned signatures.
     */
    function run()
    {
        // 1. The file's bytes must be read by the application and uploaded
        $filePath = "resources/sample.pdf";
        $fileName = basename($filePath);
        $file = fopen($filePath, "r");
        $uploadModel = new UploadModel($this->signerClient->uploadFile($fileName, $file, "application/pdf"));

        // 2. Define the name of the document which will be visible in the application
        $fileUploadModelBuilder = new FileUploadBuilder($uploadModel);
        $fileUploadModelBuilder->setDisplayName("One Signer Sample");

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

        // 5.  Create the mark atributes
        $documentMark = new DocumentMarkPrePositionedDocumentMarkModel();
        $documentMark->setType(DocumentMarkType::SIGNATURE_VISUAL_REPRESENTATION); //This is the attribute responsible for defining the Type of signature you are going to use
        $documentMark->setUploadId($fileUploadModelBuilder->getId()); //Document id
        $documentMark->setTopLeftX(395.0); //Signature position, in pixels, over the X axis
        $documentMark->setTopLeftY(560.0); //Signature position, in pixels, over the Y axis
        $documentMark->setWidth(170.0); //Width of the rectangle where signature will be placed in (It already has a default value)
        $documentMark->setHeight(94.0); //Height of the rectangle where signature will be placed in (It already has a default value)
        $documentMark->setPageNumber(1); //Page where the signature wil be placed

        //Adding the mark attributes to the list (you can preposition marks on different documents)
        $listMarks = array($documentMark);

        $flowActionCreateModel->setPrePositionedMarks($listMarks);
        // 6. Send the document create request
        $documentRequest = new DocumentsCreateDocumentRequest();
        $documentRequest->setFiles(
            array($fileUploadModelBuilder->toModel())
        );
        $documentRequest->setFlowActions(
            array($flowActionCreateModel)
        );


        $docResult = $this->signerClient->createDocument($documentRequest)[0];

        echo "Document " . $docResult->getDocumentId() . " created\n";

    }

}