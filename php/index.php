
<?php

use Lacuna\Scenarios\CreateDocumentWithOneSignerScenario;
use Lacuna\Scenarios\DeleteDocumentScenario;
use Lacuna\Scenarios\CreateDocumentWithTwoOrMoreSignersWithoutOrderScenario;
use Lacuna\Scenarios\ListDocumentsScenario;
use Lacuna\Scenarios\Scenario;

require __DIR__ . '/vendor/autoload.php';



$signerSample = new ListDocumentsScenario();

$signerSample->Init();
$signerSample->run();









