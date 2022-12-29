using Lacuna.Signer.Api;
using Lacuna.Signer.Api.Webhooks;
using Newtonsoft.Json;
using System.Threading.Tasks;

namespace SignerDemo.Scenarios
{
    public class CheckDocumentStatusScenario : Scenario, IWebhookHandlerScenario
    {
        /**
         * This scenario demonstrates how to check if a document is concluded, approved, refused or signed 
         * and the status of it's flow actions.
         */
        public override async Task RunAsync()
        {
            var result = await CreateDocumentAsync();

            // 1. Get the document's details by it's id
            var details = await SignerClient.GetDocumentDetailsAsync(result.DocumentId);

            // 2. Check if the whole flow is concluded
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
             * The best way to know the exact time a document's flow is concluded, signed, approved or refused is by enabling a webhook in your organization on the
             * application. Whenever the flow of a document has one of these steps done, the application will fire a Webhook event by
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
                    var concludedDocument = JsonConvert.DeserializeObject<DocumentConcludedModel>(webhook.Data.ToString());
                    System.Console.WriteLine($"Document {concludedDocument.Id} is concluded!");
                } 
                else if (webhook.Type == WebhookTypes.DocumentRefused) 
                {
                    var refusedDocument = JsonConvert.DeserializeObject<DocumentRefusedModel>(webhook.Data.ToString());
                    System.Console.WriteLine($"Document {refusedDocument.Id} is refused!");
                }
                else if (webhook.Type == WebhookTypes.DocumentApproved)
                {
                    var approvedDocument = JsonConvert.DeserializeObject<DocumentApprovedModel>(webhook.Data.ToString());
                    System.Console.WriteLine($"Document {approvedDocument.Id} is approved!");
                }
                else if (webhook.Type == WebhookTypes.DocumentSigned)
                {
                    var signedDocument = JsonConvert.DeserializeObject<DocumentSignedModel>(webhook.Data.ToString());
                    System.Console.WriteLine($"Document {signedDocument.Id} is signed!");
                }
            }
        }
    }
}
