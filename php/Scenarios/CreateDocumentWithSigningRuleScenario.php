<?php


namespace Lacuna\Scenarios;


use Lacuna\Signer\Model\DocumentsCreateDocumentRequest;
use Lacuna\Signer\Model\FlowActionsFlowActionCreateModel;
use Lacuna\Signer\Model\FlowActionType;
use Lacuna\Signer\Model\UsersParticipantUserModel;
use Lacuna\Signer\PhpClient\Builders\FileUploadBuilder;
use Lacuna\Signer\PhpClient\Models\UploadModel;

class CreateDocumentWithSigningRuleScenario extends Scenario
{
    /**
     * This scenario demonstrates the creation of a document
     * that will be signed by a sign rule.
     * In a signing rule multiples users are assigned to the
     * same action but just an arbitrary number of them are
     * required to sign in order to complete that action.
     */
    function run()
    {
        // 1. The file's bytes must be read by the application and uploaded
        $filePath = "sample.pdf";
        $fileName = basename($filePath);
        $file = fopen($filePath, "r");
        $uploadModel = new UploadModel($this->signerClient->uploadFile($fileName, $file, "application/pdf"));

        // 2. Define the name of the document which will be visible in the application
        $fileUploadModelBuilder = new FileUploadBuilder($uploadModel);
        $fileUploadModelBuilder->setDisplayName("Signing Rule Sample");

        // 3. For each participant on the flow, create one instance of ParticipantUserModel
        $participantUserOne = new UsersParticipantUserModel();
        $participantUserOne->setName("Jack Bauer");
        $participantUserOne->setEmail("jack.bauer@mailinator.com");
        $participantUserOne->setIdentifier("05819884183");

        $participantUserTwo = new UsersParticipantUserModel();
        $participantUserTwo->setName("James Bond");
        $participantUserTwo->setEmail("james.bond@mailinator.com");
        $participantUserTwo->setIdentifier("95588148061");

        // 4. Each signing rule requires just one FlowActionCreateModel no matter
        //    the number of participants assigned to it. The participants are assigned to
        //    it via a list of ParticipantUserModel assigned to the `SignRuleUsers` property.
        //    The number of required signatures from this list of participants is represented by
        //    the property `NumberRequiredSignatures`.
        $flowActionCreateModelSigningRule = new FlowActionsFlowActionCreateModel();
        $flowActionCreateModelSigningRule->setType(FlowActionType::SIGN_RULE);
        $flowActionCreateModelSigningRule->setNumberRequiredSignatures(1);
        $flowActionCreateModelSigningRule->setSignRuleUsers(array($participantUserOne, $participantUserTwo));

        // 5. Send the document create request
        $documentRequest = new DocumentsCreateDocumentRequest();
        $documentRequest->setFiles(
            array($fileUploadModelBuilder->toModel())
        );
        $documentRequest->setFlowActions(
            array($flowActionCreateModelSigningRule)
        );

        $docResult = $this->signerClient->createDocument($documentRequest)[0];

        echo "Document " . $docResult->getDocumentId() . " created\n";

    }

}