using Lacuna.Signer.Api;
using Lacuna.Signer.Api.Documents;
using Lacuna.Signer.Api.FlowActions;
using Lacuna.Signer.Api.Users;
using System;
using System.Collections.Generic;
using System.IO;

namespace Console.Scenarios
{
    public class SubmitDocumentWithTwoOrMoreSignersWithoutOrderScenario : Scenario
    {
        public override async void Run()
        {
            var filePath = "sample.pdf";
            var fileName = Path.GetFileName(filePath);
            var file = File.ReadAllBytes(filePath);

            var uploadModel = signerClient.UploadFileAsync(fileName, file, "application/pdf");

            var fileUploadModel = new FileUploadModel(uploadModel.Result) { DisplayName = "Two Signers Without Order " + DateTime.UtcNow.ToString() };
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

            var flowActionCreateModelList = new List<FlowActionCreateModel>() {
                flowActionCreateModelOne,
                flowActionCreateModelTwo
            };

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
