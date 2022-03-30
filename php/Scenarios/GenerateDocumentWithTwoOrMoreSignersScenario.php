<?php


namespace Lacuna\Scenarios;

use Lacuna\Scenarios\DocumentGeneration\DocumentsGenerationDocumentModel;
use Lacuna\Scenarios\DocumentGeneration\GenerateUploadModel;
use Lacuna\Scenarios\DocumentGeneration\DocumentGenerationModel;
use Lacuna\Signer\Model\DocumentsCreateDocumentRequest;
use Lacuna\Signer\Model\DocumentsCreateDocumentResult;
use Lacuna\Signer\Model\FlowActionsFlowActionCreateModel;
use Lacuna\Signer\Model\FlowActionType;
use Lacuna\Signer\Model\UsersParticipantUserModel;
use Lacuna\Signer\PhpClient\Builders\FileUploadBuilder;
use Lacuna\Signer\PhpClient\Models\UploadModel;
use Lacuna\Signer\Model\CertificateTypes;

class GenerateDocumentWithTwoOrMoreSignersScenario extends Scenario
{
    /**
     * This scenario demonstrates the creation of a document with one signer.
     */
    function run()
    {
         // 1. The file's bytes must be read by the application and uploaded
         $modelFilePath = "resources/fase1modelo.pdf";
         $fileName = basename($modelFilePath);
         $file = fopen($modelFilePath, "r");
         $templateUploadModel = new UploadModel($this->signerClient->uploadFile($fileName, $file, "application/pdf"));
         echo "Document pdf " . $templateUploadModel->getName() . " created\n";
         fclose($file);
 
         // 2. Define the name of the document which will be visible in the application
         $templateFileUploadModelBuilder = new DocumentGenerationModel($templateUploadModel);
         $templateFileUploadModelBuilder->setDisplayName("fase1modelo.pdf");
         $templateFileUploadModelBuilder->setSize(941650);
         $templateFileUploadModelBuilder->setPercentDone(100);

         // 3. Create a data file which contains the data for the template
         $dataFilePath = "resources/fase1_dados.csv";
         $fileName = basename($dataFilePath);
         $file = fopen($dataFilePath, "r");
         $dataUploadModel = new UploadModel($this->signerClient->uploadFile($fileName, $file, "text/csv"));
         echo "Document pdf " . $dataUploadModel->getName() . " created\n";
         fclose($file);

          // 4. Define the name of the data document which will be visible in the application
         $dataFileUploadModelBuilder = new DocumentGenerationModel($dataUploadModel);
         $dataFileUploadModelBuilder->setDisplayName("fase1_dados.csv");
         $dataFileUploadModelBuilder->setSize(37);
         $dataFileUploadModelBuilder->setPercentDone(100);

        // DEBUG
        //  echo 'templateFile('. json_encode( $templateFileUploadModelBuilder ) .')'. "\n\n";
        //  echo 'dataFile('. json_encode( $dataFileUploadModelBuilder ) .')'. "\n";

         // 5. For each participant on the flow, create one instance of ParticipantUserModel
         $participantUserOne = new UsersParticipantUserModel();
         $participantUserOne->setName("Jack Bauer");
         $participantUserOne->setEmail("jack.bauer@mailinator.com");
         $participantUserOne->setIdentifier("75502846369");

        // 5.1. A new participant is added
        $participantUserTwo = new UsersParticipantUserModel();
        $participantUserTwo->setName("James Bond");
        $participantUserTwo->setEmail("james.bond@mailinator.com");
        $participantUserTwo->setIdentifier("95588148061");

        // 6. Create a FlowActionCreateModel instance for each action (signature or approval) in the flow.
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

        // 7. Create a generation document request
        $generationRequest = new DocumentsGenerationDocumentModel();
        // 7.2. Set Data File
        $generationRequest->setTemplateFile(
            $templateFileUploadModelBuilder->toModel()
        );
        // 7.2. Set Data File
        $generationRequest->setDataFile(
            $dataFileUploadModelBuilder->toModel()
        );
        // 7.3. Set flow actions
        $generationRequest->setFlowActions(
            array($flowActionCreateModelOne, $flowActionCreateModelTwo)
        );

        // 7.4 Set other parameters
        $generationRequest->setObservers(array());
        $generationRequest->setFolderId("");
        $generationRequest->setNewFolderName("new folder");
        $generationRequest->setOrganizationId("");
        $generationRequest->setType("Deed");
        $generationRequest->setNotaryType(null);
        $generationRequest->setExpirationDate("2099-12-12");
        $generationRequest->setNotifiedEmails(array());

        // DEBUG
        // echo $generationRequest . "\n";

        $docResult = $this->signerClient->generateDocument($generationRequest);
        $json = json_encode($docResult, JSON_PRETTY_PRINT);

        // Display the output
        echo($json);
    }
}
