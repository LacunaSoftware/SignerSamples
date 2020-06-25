using Lacuna.Signer.Api;
using Lacuna.Signer.Api.Webhooks;
using System.Threading.Tasks;

namespace Console.Scenarios
{
    public class CheckDocumentStatusScenario : Scenario, IWebhookHandlerScenario
    {
        /**
         * This scenario shows step by step instructions to check if a document is concluded 
         * and the status of it's flow actions.
         */
        public override async Task RunAsync()
        {
            var result = await createDocumentAsync();

            // 1. Get the document's details by it's id
            var details = await signerClient.GetDocumentDetailsAsync(result.DocumentId);

            // 2. Check if the whole flow is conclued
            if (details.IsConcluded)
            {

            }

            // 3. If needed, check the status of individual flow actions
            foreach (var flowAction in details.FlowActions)
            {
                if (flowAction.Status == ActionStatus.Completed)
                {

                }
            }

            /**
             * NOTE: 
             * 
             * The best way to know the exact time a document's flow is concluded is by enabling a webhook in your organization on the
             * application. Whenever the flow of a document is completed, the application will fire a Webhook event by
             * sending a POST request to a registered URL.
             * 
             * You can find below an example of the handling logic of a webhook event.
             * 
             * Access the following link for information on available Webhook events:
             * https://dropsigner.com/swagger
             */
        }

        public void HandleWebhook(WebhookModel webhook)
        {
            if (webhook != null)
            {
                if (webhook.Type == WebhookTypes.DocumentConcluded)
                {
                    var concludedDocument = (DocumentConcludedModel)webhook.Data;

                    System.Console.WriteLine($"Document {concludedDocument.Id} is concluded!");
                }
            }
        }
    }
}
