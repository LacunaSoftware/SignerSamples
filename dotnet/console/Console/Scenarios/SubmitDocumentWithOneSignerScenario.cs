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

namespace Console.Scenarios
{
    public class SubmitDocumentWithOneSignerScenario : Scenario
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

            var fileUploadModel = new FileUploadModel(uploadModel.Result) { DisplayName = "One Signer " + DateTime.UtcNow.ToString() };
            var fileUploadModelList = new List<FileUploadModel>() { fileUploadModel };

            var participantUser = new ParticipantUserModel()
            {
                Name = "Jack Bauer",
                Email = "jack.bauer@mailinator.com",
                Identifier = "75502846369"
            };

            var flowActionCreateModel = new FlowActionCreateModel()
            {
                Type = FlowActionType.Signer,
                User = participantUser
            };

            var flowActionCreateModelList = new List<FlowActionCreateModel>() { flowActionCreateModel };

            var documentRequest = new CreateDocumentRequest()
            {
                Files = fileUploadModelList,
                FlowActions = flowActionCreateModelList
            };
            var documentIds = signerClient.CreateDocumentAsync(documentRequest);

            var documentId = documentIds.Result[0].DocumentId;
            var details = signerClient.GetDocumentDetailsAsync(documentId);
            var flowAction = details.Result.FlowActions[0];
            await signerClient.SendFlowActionReminderAsync(documentId, flowAction.Id);
        }
    }
}
