<?php

// Entry point of the webhook endpoint sample (Scenarios/WebhookEndpointScenario.php). From this
// folder, serve it with PHP's built-in web server:
//
//     php -S localhost:8080 webhook.php
//
// and post events to http://localhost:8080/webhook.

use Lacuna\Scenarios\WebhookEndpointScenario;

require __DIR__ . '/vendor/autoload.php';

$webhookEndpoint = new WebhookEndpointScenario();
$webhookEndpoint->init();
$webhookEndpoint->run();
