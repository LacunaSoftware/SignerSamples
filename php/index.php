
<?php

use Lacuna\Scenarios\CheckDocumentStatusScenario;
use Lacuna\Scenarios\CreateDigitalDegreeScenario;
use Lacuna\Scenarios\CreateDocumentInExistingFolder;
use Lacuna\Scenarios\CreateDocumentInNewFolderScenario;
use Lacuna\Scenarios\CreateDocumentWithApproversScenario;
use Lacuna\Scenarios\CreateDocumentWithAttachmentScenario;
use Lacuna\Scenarios\CreateDocumentWithDescriptionScenario;
use Lacuna\Scenarios\CreateDocumentWithOneSignerScenario;
use Lacuna\Scenarios\CreateDocumentWithPositionedSignaturesScenario;
use Lacuna\Scenarios\CreateDocumentWithTwoOrMoreSignersWithOrderScenario;
use Lacuna\Scenarios\CreatePDFDocumentWithCadesSignatureScenario;
use Lacuna\Scenarios\DeleteDocumentScenario;
use Lacuna\Scenarios\CreateDocumentWithTwoOrMoreSignersWithoutOrderScenario;
use Lacuna\Scenarios\ListDocumentsScenario;
use Lacuna\Scenarios\Scenario;

require __DIR__ . '/vendor/autoload.php';



$signerSample = new CreateDocumentWithOneSignerScenario();

$signerSample->init();
$signerSample->run();









