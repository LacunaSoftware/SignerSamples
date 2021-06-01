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
    /**
     * This scenario demonstrates the creation of a document with
     * two signers and without a particular order for the signatures.
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
        $fileUploadModelBuilder->setDisplayName("Two Signers Without Order Sample");

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

        $docResult = $this->signerClient->createDocument($documentRequest)[0];

        echo "Document " . $docResult->getDocumentId() . " created\n";
    }

}