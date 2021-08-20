<?php


namespace Lacuna\Scenarios;


use Lacuna\Signer\Model\DocumentsDocumentAddVersionRequest;
use Lacuna\Signer\PhpClient\Builders\FileUploadBuilder;
use Lacuna\Signer\PhpClient\Models\UploadModel;

class AddNewDocumentVersionScenario extends Scenario
{
    /**
     * This scenario demonstrates how to add a new document version to a existing document (the flow will be reset) .
     */
    function run()
    {
        // 1. You need a document id
        $document = $this->createDocument();
        $documentId = $document->getDocumentId();


        // 2. The file's bytes must be read by the application and uploaded
        $filePath = "resources/sample.pdf";
        $fileName = basename($filePath);
        $file = fopen($filePath, "r");
        $uploadModel = new UploadModel($this->signerClient->uploadFile($fileName, $file, "application/pdf"));
        fclose($file);

        // 3. Define the name of the document which will be visible in the application
        $fileUploadModelBuilder = new FileUploadBuilder($uploadModel);
        $fileUploadModelBuilder->setDisplayName("Add New Document Version Sample");

        // 4. Send the new version request
        $documentAddVersionRequest  = new DocumentsDocumentAddVersionRequest();
        $documentAddVersionRequest->setFile($fileUploadModelBuilder->toModel());

        $this->signerClient->addNewDocumentVersion($documentId, $documentAddVersionRequest);

    }
}