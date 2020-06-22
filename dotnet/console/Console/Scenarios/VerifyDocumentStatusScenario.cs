using Lacuna.Signer.Api;
using Lacuna.Signer.Api.Documents;
using Lacuna.Signer.Api.FlowActions;
using Lacuna.Signer.Api.Users;
using System;
using System.Collections.Generic;
using System.IO;

namespace Console.Scenarios
{
    public class VerifyDocumentStatusScenario : Scenario
    {
        /**
         * This scenario shows step-by-step the submission of a document
         * and the process to check if the flow's actions were completed.
         */
        public override void Run()
        {
            // 1. The file's bytes must be read by the application and uploaded using the method UploadFileAsync.
            var filePath = "sample.pdf";
            var fileName = Path.GetFileName(filePath);
            var file = File.ReadAllBytes(filePath);
            var uploadModel = signerClient.UploadFileAsync(fileName, file, "application/pdf");

            // 2. Signer's server expects a FileUploadModel's list to create a document.
            var fileUploadModel = new FileUploadModel(uploadModel.Result) { DisplayName = "Verify " + DateTime.UtcNow.ToString() };
            var fileUploadModelList = new List<FileUploadModel>() { fileUploadModel };

            // 3. Foreach participant on the flow, you'll need to create an instance of ParticipantUserModel.
            var approverParticipant = new ParticipantUserModel()
            {
                Name = "Jack Bauer",
                Email = "jack.bauer@mailinator.com",
                Identifier = "75502846369"
            };

            // 4. You'll need to create a FlowActionCreateModel's instance foreach ParticipantUserModel
            //    created in the previous step. The FlowActionCreateModel is responsible for holding
            //    the personal data of the participant and the type of action that it will peform on the flow.
            var flowActionCreateModelApprover = new FlowActionCreateModel()
            {
                Type = FlowActionType.Approver,
                User = approverParticipant
            };

            // 5. Signer's server expects a FlowActionCreateModel's list to create a document.
            var flowActionCreateModelList = new List<FlowActionCreateModel>() { flowActionCreateModelApprover };

            // 6. To create the document request, use the list of FileUploadModel and the list of FlowActionCreateModel.
            var documentRequest = new CreateDocumentRequest()
            {
                Files = fileUploadModelList,
                FlowActions = flowActionCreateModelList
            };
            var documentResults = signerClient.CreateDocumentAsync(documentRequest);

            // 7. Check for the concludeness of the flow.
            foreach (var documentResult in documentResults.Result)
            {
                // 7.1. Extracts details from the document.
                var details = signerClient.GetDocumentDetailsAsync(documentResult.DocumentId);
                
                // 7.2. Check if the whole flow is conclued.
                if (details.Result.IsConcluded)
                {

                }

                // 7.3. Check if each flow action individualy is completed.
                foreach (var flowAction in details.Result.FlowActions)
                {
                    if (flowAction.Status == ActionStatus.Completed)
                    {

                    }
                }
            }
        }
    }
}
