<?php


namespace Lacuna\Scenarios;


use Lacuna\Signer\Model\DocumentsCreateDocumentRequest;
use Lacuna\Signer\Model\FlowActionsFlowActionCreateModel;
use Lacuna\Signer\Model\FlowActionsXadesOptionsModel;
use Lacuna\Signer\Model\FlowActionType;
use Lacuna\Signer\Model\UsersParticipantUserModel;
use Lacuna\Signer\Model\XadesElementIdentifierTypes;
use Lacuna\Signer\Model\XadesSignatureTypes;
use Lacuna\Signer\PhpClient\Builders\FileUploadBuilder;
use Lacuna\Signer\PhpClient\Models\UploadModel;

class CreateXmlWithFullDocumentSignatureScenario extends Scenario
{
    /**
     * This scenario demonstrates the creation of a document
     * that needs to be signed using the XAdES format for
     * the full XML file.
     */
    function run()
    {
        // 1. The file's bytes must be read by the application and uploaded
        $filePath = "resources/sample.xml";
        $fileName = basename($filePath);
        $file = fopen($filePath, "r");
        $uploadModel = new UploadModel($this->signerClient->uploadFile($fileName, $file, "application/xml"));
        fclose($file);

        // 2. Define the name of the document which will be visible in the application
        $fileUploadModelBuilder = new FileUploadBuilder($uploadModel);
        $fileUploadModelBuilder->setDisplayName("XML Full Signature Sample");

        // 3. For each participant on the flow, create one instance of ParticipantUserModel
        $user = new UsersParticipantUserModel();
        $user->setName("Jack Bauer");
        $user->setEmail("jack.bauer@mailinator.com");
        $user->setIdentifier("75502846369");

        // 4. Specify the signature type
        $xadesOptionsModel = new FlowActionsXadesOptionsModel();
        $xadesOptionsModel->setSignatureType(XadesSignatureTypes::FULL_XML);

        // 5. Create a FlowActionCreateModel instance for each action (signature or approval) in the flow.
        //    This object is responsible for defining the personal data of the participant and the type of
        //    action that he will perform on the flow.
        $flowActionCreateModel = new FlowActionsFlowActionCreateModel();
        $flowActionCreateModel->setType(FlowActionType::SIGNER);
        $flowActionCreateModel->setUser($user);
        $flowActionCreateModel->setXadesOptions($xadesOptionsModel);

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