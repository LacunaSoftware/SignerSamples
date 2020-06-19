using Lacuna.Signer.Api;
using Lacuna.Signer.Api.Documents;
using Lacuna.Signer.Api.FlowActions;
using Lacuna.Signer.Api.Users;
using System;
using System.Collections.Generic;
using System.IO;

namespace Console.Scenarios
{
    public class SubmitDocumentWithApproversScenario : Scenario
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

            var fileUploadModel = new FileUploadModel(uploadModel.Result) { DisplayName = "Approver + Signer " + DateTime.UtcNow.ToString() };
            var fileUploadModelList = new List<FileUploadModel>() { fileUploadModel };

            var signerParticipant = new ParticipantUserModel()
            {
                Name = "Jack Bauer",
                Email = "jack.bauer@mailinator.com",
                Identifier = "75502846369"
            };

            var approverParticipant = new ParticipantUserModel()
            {
                Name = "Gary Eggsy",
                Email = "gary.eggsy@mailinator.com",
                Identifier = "04564219049"
            };

            var flowActionCreateModelSigner = new FlowActionCreateModel()
            {
                Type = FlowActionType.Signer,
                User = signerParticipant
            };

            var flowActionCreateModelApprover = new FlowActionCreateModel()
            {
                Type = FlowActionType.Approver,
                User = approverParticipant
            };

            var flowActionCreateModelList = new List<FlowActionCreateModel>() {
                flowActionCreateModelSigner,
                flowActionCreateModelApprover
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
