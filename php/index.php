
<?php

use Lacuna\Scenarios\CreateDocumentWithOneSignerScenario;
use Lacuna\Scenarios\Scenario;

require __DIR__ . '/vendor/autoload.php';



$signerSample = new CreateDocumentWithOneSignerScenario();

$signerSample->Init();
$signerSample->run();









