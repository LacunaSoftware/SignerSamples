using Lacuna.Signer.Api;
using Lacuna.Signer.Api.Documents;
using Lacuna.Signer.Api.FlowActions;
using Lacuna.Signer.Api.Users;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SignerDemo.Scenarios
{
    class CreateDocumentWithDescriptionScenario : Scenario
    {
        /**
         * This scenario demonstrates the creation of a document with description
         * 
         */
        public override async Task RunAsync()
        {
            // 1. The file's bytes must be read by the application and uploaded
            var filePath = "sample.pdf";
            var fileName = Path.GetFileName(filePath);
            var file = File.ReadAllBytes(filePath);
            var uploadModel = await SignerClient.UploadFileAsync(fileName, file, "application/pdf");

            // 2. Define the name of the document which will be visible in the application
            var fileUploadModel = new FileUploadModel(uploadModel) { DisplayName = "One Description Sample" };

            // 3. For each participant on the flow, create one instance of ParticipantUserModel
            var participantUser = new ParticipantUserModel()
            {
                Name = "Jack Bauer",
                Email = "jack.bauer@mailinator.com",
                Identifier = "75502846369"
            };

            // 4. Create a FlowActionCreateModel instance for each action (signature or approval) in the flow.
            var flowActionCreateModel = new FlowActionCreateModel()
            {
                Type = FlowActionType.Signer,
                User = participantUser
            };

            // 5. Send the document create request writing the description as a string 
            var documentRequest = new CreateDocumentRequest()
            {
                Files = new List<FileUploadModel>() { fileUploadModel },
                Description = "Some Description Sample", 
                FlowActions = new List<FlowActionCreateModel>() { flowActionCreateModel }
            };
            var result = (await SignerClient.CreateDocumentAsync(documentRequest)).First();

            System.Console.WriteLine($"Document {result.DocumentId} created");
        }
    }
}
