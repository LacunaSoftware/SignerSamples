using Lacuna.Signer.Api;
using Lacuna.Signer.Api.Documents;
using Lacuna.Signer.Api.FlowActions;
using Lacuna.Signer.Api.Users;
using Lacuna.Signer.Api.Webhooks;
using System;
using System.Collections.Generic;
using System.IO;

namespace Console.Scenarios
{
    public class CheckDocumentStatusScenario : Scenario
    {
        /**
         * This scenario shows step-by-step instructions to create a document
         * and how to check if the flow's actions are completed.
         */
        public override void Run()
        {
            // 1. The file's bytes must be read by the application and uploaded
            var filePath = "sample.pdf";
            var fileName = Path.GetFileName(filePath);
            var file = File.ReadAllBytes(filePath);
            var uploadModel = signerClient.UploadFileAsync(fileName, file, "application/pdf");

            // 2. Define the name of the document which will be visible in the application
            var documentModel = new FileUploadModel(uploadModel.Result) { DisplayName = "Check Status Sample " + DateTime.UtcNow.ToString() };

            // 3. For each participant on the flow, create one instance of ParticipantUserModel.
            var approverParticipant = new ParticipantUserModel()
            {
                Name = "Jack Bauer",
                Email = "jack.bauer@mailinator.com",
                Identifier = "75502846369"
            };

            // 4. Create a FlowActionCreateModel instance for each action (signature or approval) in the flow.
            //    This object is responsible for defining the personal data of the participant and the type of 
            //    action that he will peform on the flow.
            var flowActionCreateModelApprover = new FlowActionCreateModel()
            {
                Type = FlowActionType.Approver,
                User = approverParticipant
            };

            // 5. Send the document create request
            var documentRequest = new CreateDocumentRequest()
            {
                Files = new List<FileUploadModel>() { documentModel },
                FlowActions = new List<FlowActionCreateModel>() { flowActionCreateModelApprover }
            };
            var documentResults = signerClient.CreateDocumentAsync(documentRequest);

            // 6. Check the status of the flow
            foreach (var documentResult in documentResults.Result)
            {
                // 6.1. Get the document's details
                var details = signerClient.GetDocumentDetailsAsync(documentResult.DocumentId);
                
                // 6.2. Check if the whole flow is conclued
                if (details.Result.IsConcluded)
                {

                }

                // 6.3. If needed, check the status of individual flow actions
                foreach (var flowAction in details.Result.FlowActions)
                {
                    if (flowAction.Status == ActionStatus.Completed)
                    {

                    }
                }
            }

            /**
             * NOTE: 
             * 
             * The best way to know the exact time a document's flow is concluded is by enabling a webhook in your organization on the
             * application. Whenever the flow of a document is completed, the application will fire a Webhook event by
             * sending a POST request to a registered URL.
             * 
             * You can find bellow an example of the handling logic of a webhook event.
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
