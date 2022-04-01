<?php


namespace Lacuna\Scenarios;


use Lacuna\Signer\Model\GenerateDocumentRequest;
use Lacuna\Signer\Model\DocumentBatchStatus;
use Lacuna\Signer\Model\FlowActionsFlowActionCreateModel;
use Lacuna\Signer\Model\FlowActionType;
use Lacuna\Signer\Model\GenerationDocumentResult;
use Lacuna\Signer\Model\UsersParticipantUserModel;
use Lacuna\Signer\PhpClient\Builders\FileUploadBuilder;
use Lacuna\Signer\PhpClient\Models\UploadModel;
use Lacuna\Signer\PhpClient;




class GenerateDocumentWithTwoOrMoreSignersScenario extends Scenario
{
    
    /**
     * This scenario demonstrates the generation of a document with one signer.
     */
    
    
     function run()
    {
         // 1. The Pdf form template bytes must be read by the application and uploaded.
         $modelFilePath = "resources/Contrato-Servicos.pdf";
         $fileName = basename($modelFilePath);
         $file = fopen($modelFilePath, "r");
         $templateUploadModel = new UploadModel($this->signerClient->uploadFile($fileName, $file, "application/pdf"));
         echo "Pdf File " . $templateUploadModel->getName() . " uploaded\n";
         fclose($file);
         $templateFileUploadModelBuilder = new FileUploadBuilder($templateUploadModel);
 
         // 2. Upload the Csv data file which contains the data for the template.
         $dataFilePath = "resources/Contrato-Servicos.csv";
         $fileName = basename($dataFilePath);
         $file = fopen($dataFilePath, "r");
         $dataUploadModel = new UploadModel($this->signerClient->uploadFile($fileName, $file, "text/csv"));
         echo "Csv File " . $dataUploadModel->getName() . " uploaded\n";
         fclose($file);
         $dataFileUploadModelBuilder = new FileUploadBuilder($dataUploadModel);
         
         // 4. For each participant on the flow, create one instance of ParticipantUserModel
          $participantUserOne = new UsersParticipantUserModel();
          $participantUserOne->setName("Jack Bauer");
          $participantUserOne->setEmail("jack.bauer@mailinator.com");
          $participantUserOne->setIdentifier("75502846369");
 
         // 4.1. A new participant is added
         $participantUserTwo = new UsersParticipantUserModel();
         $participantUserTwo->setName("James Bond");
         $participantUserTwo->setEmail("james.bond@mailinator.com");
         $participantUserTwo->setIdentifier("95588148061");
       
         // 5. Create a FlowActionCreateModel instance for each action (signature or approval) in the flow.
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
       
         // 6. Create a generation document request
         $generationRequest = new GenerateDocumentRequest();
         
         // 6.1. Set Template File
         $generationRequest->setTemplateFile(
         
             $templateFileUploadModelBuilder->toModel()
             
         );
         // 6.2. Set Data File
         $generationRequest->setDataFile(
             $dataFileUploadModelBuilder->toModel()
         );
         // 6.3. Set flow actions
         $generationRequest->setFlowActions(
             array($flowActionCreateModelOne, $flowActionCreateModelTwo)
         );
         //echo $generationRequest;
         
        // 7. Send the document generate request
         $docResult = $this->signerClient->generateDocument($generationRequest);

         //echo $docResult;
         
        // 8. While the document is being generated, create a 10 second delay
         sleep(10);
         
        // 9. Create a new request to check the document current status
         $docResult = $this->signerClient->getGenerationStatus($docResult->getId());
         
         if($docResult->getStatus() == DocumentBatchStatus::DONE){
             echo "Document " .$docResult->getId()." generated successfully!";
         }
         
    }
}
         
         
 
         
         
         



    




       

        
