<?php


namespace Lacuna\Scenarios;


use Lacuna\Signer\Model\DocumentsCreateDocumentRequest;
use Lacuna\Signer\Model\FlowActionsFlowActionCreateModel;
use Lacuna\Signer\Model\FlowActionsXadesOptionsModel;
use Lacuna\Signer\Model\FlowActionType;
use Lacuna\Signer\Model\UsersParticipantUserModel;
use Lacuna\Signer\Model\XadesElementIdentifierTypes;
use Lacuna\Signer\Model\XadesInsertionOptions;
use Lacuna\Signer\Model\XadesSignatureTypes;
use Lacuna\Signer\Model\XmlNamespaceModel;
use Lacuna\Signer\PhpClient\Builders\FileUploadBuilder;
use Lacuna\Signer\PhpClient\Models\UploadModel;

class CreateDigitalDegreeScenario extends Scenario
{
    /**
     * This scenario demonstrates the creation of a digital degree compliant with "PORTARIA Nº 554, DE 11 DE MARÇO DE 2019".
     */

    function run()
    {
        // 1. The file's bytes must be read by the application and uploaded
        $filePath = "resources/sample-degree.xml";
        $fileName = basename($filePath);
        $file = fopen($filePath, "r");
        $uploadModel = new UploadModel($this->signerClient->uploadFile($fileName, $file, "application/xml"));

        // 2. Define the name of the document which will be visible in the application
        $fileUploadModelBuilder = new FileUploadBuilder($uploadModel);
        $fileUploadModelBuilder->setDisplayName("Digital Degree Sample");

        // 3. For each participant on the flow, create one instance of ParticipantUserModel
        $participantUserOne = new UsersParticipantUserModel();
        $participantUserOne->setName("Jack Bauer");
        $participantUserOne->setEmail("jack.bauer@mailinator.com");
        $participantUserOne->setIdentifier("75502846369");


        $participantUserTwo = new UsersParticipantUserModel();
        $participantUserTwo->setName("James Bond");
        $participantUserTwo->setEmail("james.bond@mailinator.com");
        $participantUserTwo->setIdentifier("95588148061");

        $participantUserThree = new UsersParticipantUserModel();
        $participantUserThree->setName("Garry Eggsy");
        $participantUserThree->setEmail("garry.eggsy@mailinator.com");
        $participantUserThree->setIdentifier("87657257008");

        // 4. Specify the element that holds the namespace of the issuer
        $xmlNamespaceModel = new XmlNamespaceModel();
        $xmlNamespaceModel->setPrefix("dip");
        $xmlNamespaceModel->setUri("http://portal.mec.gov.br/diplomadigital/arquivos-em-xsd");


        // 5. The fields 'DadosDiploma' and 'DadosRegistro' and the entire XML file must be signed
        $xadesOptionsDegreeData = new FlowActionsXadesOptionsModel();
        $xadesOptionsDegreeData->setSignatureType(XadesSignatureTypes::XML_ELEMENT);
        $xadesOptionsDegreeData->setElementToSignIdentifierType(XadesElementIdentifierTypes::X_PATH);
        $xadesOptionsDegreeData->setElementToSignIdentifier("//dip:DadosDiploma");
        $xadesOptionsDegreeData->setInsertionOption(XadesInsertionOptions::APPEND_CHILD);

        $xadesOptionsModelRegisterData  = new FlowActionsXadesOptionsModel();
        $xadesOptionsModelRegisterData->setSignatureType(XadesSignatureTypes::XML_ELEMENT);
        $xadesOptionsModelRegisterData->setElementToSignIdentifierType(XadesElementIdentifierTypes::X_PATH);
        $xadesOptionsModelRegisterData->setElementToSignIdentifier("//dip:DadosRegistro");
        $xadesOptionsModelRegisterData->setInsertionOption(XadesInsertionOptions::APPEND_CHILD);

        $xadesOptionsModelFull = new FlowActionsXadesOptionsModel();
        $xadesOptionsModelFull->setSignatureType(XadesSignatureTypes::FULL_XML);

        // 6. Each signature requires its own flow action
        $degreeDataAction = new FlowActionsFlowActionCreateModel();
        $degreeDataAction->setType(FlowActionType::SIGNER);
        $degreeDataAction->setUser($participantUserOne);
        $degreeDataAction->setXadesOptions($xadesOptionsDegreeData);

        $registerDataAction  = new FlowActionsFlowActionCreateModel();
        $registerDataAction->setType(FlowActionType::SIGNER);
        $registerDataAction->setUser($participantUserTwo);
        $registerDataAction->setXadesOptions($xadesOptionsModelRegisterData);

        $flowActionCreateModelFull = new FlowActionsFlowActionCreateModel();
        $flowActionCreateModelFull->setType(FlowActionType::SIGNER);
        $flowActionCreateModelFull->setUser($participantUserThree);
        $flowActionCreateModelFull->setXadesOptions($xadesOptionsModelFull);

        // 6. Send the document create request
        $documentRequest = new DocumentsCreateDocumentRequest();
        $documentRequest->setNewFolderName("New Folder");

        $documentRequest->setFiles(
            array($fileUploadModelBuilder->toModel())
        );
        $documentRequest->setFlowActions(
            array($degreeDataAction, $registerDataAction, $flowActionCreateModelFull )
        );

        $docResult = $this->signerClient->createDocument($documentRequest)[0];

        echo "Document " . $docResult->getDocumentId() . " created\n";
    }

}