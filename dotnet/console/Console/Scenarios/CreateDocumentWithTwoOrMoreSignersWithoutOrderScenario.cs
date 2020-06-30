using Lacuna.Signer.Api;
using Lacuna.Signer.Api.Documents;
using Lacuna.Signer.Api.FlowActions;
using Lacuna.Signer.Api.Users;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Threading.Tasks;

namespace Console.Scenarios
{
    public class CreateDocumentWithTwoOrMoreSignersWithoutOrderScenario : Scenario
    {
        /**
         * This scenario demonstrates the creation of a document with 
         * two signers and without a particular order for the signatures.
         */
        public override async Task RunAsync()
        {
            // 1. The file's bytes must be read by the application and uploaded
            var filePath = "sample.pdf";
            var fileName = Path.GetFileName(filePath);
            var file = File.ReadAllBytes(filePath);
            var uploadModel = await SignerClient.UploadFileAsync(fileName, file, "application/pdf");

            // 2. Define the name of the document which will be visible in the application
            var fileUploadModel = new FileUploadModel(uploadModel) { DisplayName = "Two Signers Without Order Sample" };

            // 3. For each participant on the flow, create one instance of ParticipantUserModel
            var participantUserOne = new ParticipantUserModel()
            {
                Name = "Jack Bauer",
                Email = "jack.bauer@mailinator.com",
                Identifier = "75502846369"
            };

            var participantUserTwo = new ParticipantUserModel()
            {
                Name = "James Bond",
                Email = "james.bond@mailinator.com",
                Identifier = "95588148061"
            };

            // 4. Create a FlowActionCreateModel instance for each action (signature or approval) in the flow.
            //    This object is responsible for defining the personal data of the participant, the type of 
            //    action that he will peform on the flow and the order in which this action will take place
            //    (Step property). If the Step property of all action are the same or not specified they 
            //    may be executed at any time
            var flowActionCreateModelOne = new FlowActionCreateModel()
            {
                Type = FlowActionType.Signer,
                User = participantUserOne
            };

            var flowActionCreateModelTwo = new FlowActionCreateModel()
            {
                Type = FlowActionType.Signer,
                User = participantUserTwo
            };

            // 5. Send the document create request
            var documentRequest = new CreateDocumentRequest()
            {
                Files = new List<FileUploadModel>() { fileUploadModel },
                FlowActions = new List<FlowActionCreateModel>() 
                {
                    flowActionCreateModelOne,
                    flowActionCreateModelTwo
                }
            };
            var result = (await SignerClient.CreateDocumentAsync(documentRequest)).First();

            System.Console.WriteLine($"Document {result.DocumentId} created");
        }
    }
}
