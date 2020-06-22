using Lacuna.Signer.Api;
using Lacuna.Signer.Api.Documents;
using Lacuna.Signer.Api.FlowActions;
using Lacuna.Signer.Api.Users;
using System;
using System.Collections.Generic;
using System.IO;

namespace Console.Scenarios
{
    public class SubmitPDFDocumentWithCadesSignatureScenario : Scenario
    {
        /**
         * This scenario shows step-by-step the submission of a document
         * to the signer instance where this document it's a PDF and needs to be
         * signed with Cades.
         */
        public override async void Run()
        {
            // 1. The file's bytes must be read by the application and uploaded using the method UploadFileAsync.
            // 1.1. Select a pdf.
            var filePath = "sample.pdf";
            var fileName = Path.GetFileName(filePath);
            var file = File.ReadAllBytes(filePath);

            // 1.2. The mimeType for signing a PDF file with CAdes but me other than "application/pdf", "application/octet-stream" or even 'null' works.
            var uploadModel = signerClient.UploadFileAsync(fileName, file, "application/octet-stream");

            // 2. Signer's server expects a FileUploadModel's list to create a document.
            var fileUploadModel = new FileUploadModel(uploadModel.Result) { DisplayName = "PDF Cades " + DateTime.UtcNow.ToString() };
            var fileUploadModelList = new List<FileUploadModel>() { fileUploadModel };

            // 3. Foreach participant on the flow, you'll need to create an instance of ParticipantUserModel.
            var participantUser = new ParticipantUserModel()
            {
                Name = "Jack Bauer",
                Email = "jack.bauer@mailinator.com",
                Identifier = "75502846369"
            };

            // 4. You'll need to create a FlowActionCreateModel's instance foreach ParticipantUserModel
            //    created in the previous step. The FlowActionCreateModel is responsible for holding
            //    the personal data of the participant and the type of action that it will peform on the flow.
            var flowActionCreateModel = new FlowActionCreateModel()
            {
                Type = FlowActionType.Signer,
                User = participantUser
            };

            // 5. Signer's server expects a FlowActionCreateModel's list to create a document.
            var flowActionCreateModelList = new List<FlowActionCreateModel>() { flowActionCreateModel };

            // 6. To create the document request, use the list of FileUploadModel and the list of FlowActionCreateModel.
            var documentRequest = new CreateDocumentRequest()
            {
                Files = fileUploadModelList,
                FlowActions = flowActionCreateModelList
            };
            var documentResults = signerClient.CreateDocumentAsync(documentRequest);

            // 7. To notify the participant:
            foreach (var documentResult in documentResults.Result)
            {
                // 7.1. Extract the information of the flow using the following procedure.
                var details = signerClient.GetDocumentDetailsAsync(documentResult.DocumentId);
                foreach (var flowAction in details.Result.FlowActions)
                {
                    // 7.2. Send notification to the participant.
                    _ = signerClient.SendFlowActionReminderAsync(documentResult.DocumentId, flowAction.Id);
                }
            }
        }
    }
}
