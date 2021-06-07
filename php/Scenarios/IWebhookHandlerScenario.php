<?php


namespace Lacuna\Scenarios;


interface IWebhookHandlerScenario
{
    function HandleWebhook($webhook);
}