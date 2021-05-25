
<?php

use Lacuna\Scenarios\CheckDocumentStatusScenario;
use Lacuna\Scenarios\CreateDocumentWithApproversScenario;
use Lacuna\Scenarios\CreateDocumentWithOneSignerScenario;
use Lacuna\Scenarios\CreateDocumentWithTwoOrMoreSignersWithOrderScenario;
use Lacuna\Scenarios\CreatePDFDocumentWithCadesSignatureScenario;
use Lacuna\Scenarios\DeleteDocumentScenario;
use Lacuna\Scenarios\CreateDocumentWithTwoOrMoreSignersWithoutOrderScenario;
use Lacuna\Scenarios\ListDocumentsScenario;
use Lacuna\Scenarios\Scenario;

require __DIR__ . '/vendor/autoload.php';



$signerSample = new CreateDocumentWithTwoOrMoreSignersWithoutOrderScenario();

$signerSample->init();
$signerSample->run();









