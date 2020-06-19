using Lacuna.Signer.Api;
using Lacuna.Signer.Api.Documents;
using Lacuna.Signer.Api.FlowActions;
using Lacuna.Signer.Api.Users;
using System;
using System.Collections.Generic;
using System.IO;

namespace Console.Scenarios
{
    public class SubmitDocumentWithSigningRuleScenario : Scenario
    {
        public override async void Run()
        {
            var filePath = "sample.pdf";
            var fileName = Path.GetFileName(filePath);
            var file = File.ReadAllBytes(filePath);

            var uploadModel = signerClient.UploadFileAsync(fileName, file, "application/pdf");

            var fileUploadModel = new FileUploadModel(uploadModel.Result) { DisplayName = "Signing Rule " + DateTime.UtcNow.ToString() };
            var fileUploadModelList = new List<FileUploadModel>() { fileUploadModel };

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

            var flowActionCreateModelSigningRule = new FlowActionCreateModel()
            {
                Type = FlowActionType.SignRule,
                NumberRequiredSignatures = 1,
                SignRuleUsers = new List<ParticipantUserModel>() { participantUserOne, participantUserTwo }
            };

            var flowActionCreateModelList = new List<FlowActionCreateModel>() { flowActionCreateModelSigningRule };

            var documentRequest = new CreateDocumentRequest()
            {
                Files = fileUploadModelList,
                FlowActions = flowActionCreateModelList
            };
            var documentResults = signerClient.CreateDocumentAsync(documentRequest);

            foreach (var documentResult in documentResults.Result)
            {
                var details = signerClient.GetDocumentDetailsAsync(documentResult.DocumentId);
                var flowAction = details.Result.FlowActions[0];
                await signerClient.SendFlowActionReminderAsync(documentResult.DocumentId, flowAction.Id);
            }
        }
    }
}
