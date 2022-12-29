using Lacuna.Signer.Api.Webhooks;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SignerDemo.Scenarios
{
    public interface IWebhookHandlerScenario
    {
        void HandleWebhook(WebhookModel webhook);
    }
}
