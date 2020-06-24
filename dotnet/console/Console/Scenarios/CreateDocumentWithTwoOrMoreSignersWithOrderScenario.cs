using Lacuna.Signer.Api;
using Lacuna.Signer.Api.Documents;
using Lacuna.Signer.Api.FlowActions;
using Lacuna.Signer.Api.Users;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Threading.Tasks;

namespace Console.Scenarios
{
    public class CreateDocumentWithTwoOrMoreSignersWithOrderScenario : Scenario
    {
        /**
         * This scenario shows step-by-step the submission of a document
         * to the signer instance where there are two participant in the role
         * of signatories and the there's a required order for the signatures.
         */
        public override async Task RunAsync()
        {
            // 1. The file's bytes must be read by the application and uploaded
            var filePath = "sample.pdf";
            var fileName = Path.GetFileName(filePath);
            var file = File.ReadAllBytes(filePath);
            var uploadModel = await signerClient.UploadFileAsync(fileName, file, "application/pdf");

            // 2. Define the name of the document which will be visible in the application
            var fileUploadModel = new FileUploadModel(uploadModel) { DisplayName = "Two Signers With Order Sample" };

            // 3. For each participant on the flow, create one instance of ParticipantUserModel.
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

            // 4. You'll need to create a FlowActionCreateModel's instance foreach ParticipantUserModel
            //    created in the previous step. The FlowActionCreateModel is responsible for holding
            //    the personal data of the participant and the type of action that it will peform on the flow.
            //    In the case of order for the flow actions it's necessary to assign values to the `Step` propertie
            //    of the instances, smaller numbers represents action that comes first.
            var flowActionCreateModelOne = new FlowActionCreateModel()
            {
                Type = FlowActionType.Signer,
                User = participantUserOne,
                Step = 1
            };

            var flowActionCreateModelTwo = new FlowActionCreateModel()
            {
                Type = FlowActionType.Signer,
                User = participantUserTwo,
                Step = 2
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
            var result = (await signerClient.CreateDocumentAsync(documentRequest)).First();

            System.Console.WriteLine($"Document {result.DocumentId} created");
        }
    }
}
