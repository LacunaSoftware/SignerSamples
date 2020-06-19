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

            var mimeType = "application/octet-stream";
            if (fileName.EndsWith(".pdf"))
            {
                mimeType = "application/pdf";
            }

            var uploadModel = signerClient.UploadFileAsync(fileName, file, mimeType);

            var fileUploadModel = new FileUploadModel(uploadModel.Result) { DisplayName = "Two Signers With Order " + DateTime.UtcNow.ToString() };
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

            var documentId = documentResults.Result[0].DocumentId;
            var details = signerClient.GetDocumentDetailsAsync(documentId);
            var flowAction = details.Result.FlowActions[0];
            await signerClient.SendFlowActionReminderAsync(documentId, flowAction.Id);
        }
    }
}
