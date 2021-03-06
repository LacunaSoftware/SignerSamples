﻿using Lacuna.Signer.Api;
using Lacuna.Signer.Api.Documents;
using Lacuna.Signer.Api.FlowActions;
using Lacuna.Signer.Api.Users;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Threading.Tasks;

namespace Console.Scenarios
{
    public class CreateDocumentWithSigningRuleScenario : Scenario
    {
        /**
         * This scenario demonstrates the creation of a document
         * that will be signed by a sign rule.
         * In a signing rule multiples users are assigned to the 
         * same action but just an arbitrary number of them are 
         * required to sign in order to complete that action.
         */
        public override async Task RunAsync()
        {
            // 1. The file's bytes must be read by the application and uploaded
            var filePath = "sample.pdf";
            var fileName = Path.GetFileName(filePath);
            var file = File.ReadAllBytes(filePath);
            var uploadModel = await SignerClient.UploadFileAsync(fileName, file, "application/pdf");

            // 2. Define the name of the document which will be visible in the application
            var fileUploadModel = new FileUploadModel(uploadModel) { DisplayName = "Signing Rule Sample" };

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

            // 4. Each signing rule requires just one FlowActionCreateModel no matter
            //    the number of participants assigned to it. The participants are assigned to
            //    it via a list of ParticipantUserModel assigned to the `SignRuleUsers` property.
            //    The number of required signatures from this list of participants is represented by
            //    the property `NumberRequiredSignatures`.
            var flowActionCreateModelSigningRule = new FlowActionCreateModel()
            {
                Type = FlowActionType.SignRule,
                NumberRequiredSignatures = 1,
                SignRuleUsers = new List<ParticipantUserModel>() { participantUserOne, participantUserTwo }
            };

            // 5. Send the document create request
            var documentRequest = new CreateDocumentRequest()
            {
                Files = new List<FileUploadModel>() { fileUploadModel },
                FlowActions = new List<FlowActionCreateModel>() { flowActionCreateModelSigningRule }
            };
            var result = (await SignerClient.CreateDocumentAsync(documentRequest)).First();

            System.Console.WriteLine($"Document {result.DocumentId} created");
        }
    }
}
