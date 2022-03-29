<?php


namespace Lacuna\Scenarios;


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
        echo "Hello world";
    }
}
