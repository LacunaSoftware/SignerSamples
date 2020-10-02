package com.lacunasoftware.signer.sample.scenarios;

import com.lacunasoftware.signer.webhooks.WebhookModel;

public interface IWebhookHandlerScenario {
    void HandleWebhook(WebhookModel webhook);
}