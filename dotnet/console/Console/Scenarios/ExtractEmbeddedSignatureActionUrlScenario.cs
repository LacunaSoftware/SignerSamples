using Lacuna.Signer.Api;
using Lacuna.Signer.Api.Documents;
using Lacuna.Signer.Api.FlowActions;
using Lacuna.Signer.Api.Users;
using System;
using System.Collections.Generic;
using System.IO;

namespace Console.Scenarios
{
    public class ExtractEmbeddedSignatureActionUrlScenario : Scenario
    {
        /**
         * This scenario shows step-by-step the submission of a document
         * and the extraction of it's action url for embed signature purposes.
         */
        public override void Run()
        {
            // 1. The file's bytes must be read by the application and uploaded using the method UploadFileAsync.
            var filePath = "sample.pdf";
            var fileName = Path.GetFileName(filePath);
            var file = File.ReadAllBytes(filePath);
            var uploadModel = signerClient.UploadFileAsync(fileName, file, "application/pdf");

            // 2. Signer's server expects a FileUploadModel's list to create a document.
            var fileUploadModel = new FileUploadModel(uploadModel.Result) { DisplayName = "One Signer " + DateTime.UtcNow.ToString() };
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

            // 7. Extract the documentId
            var documentResults = signerClient.CreateDocumentAsync(documentRequest);
            var documentId = documentResults.Result[0].DocumentId;

            // 8. Send a request using the following procedure, the actionUrlResponse object holds the url
            //    necessary for to embed the signature into your application. 
            var actionUrlRequest = new ActionUrlRequest()
            {
                Identifier = participantUser.Identifier,
                EmailAddress = participantUser.Email
            };
            var actionUrlResponse = signerClient.GetActionUrlAsync(documentId, actionUrlRequest).Result;
        }
    }
}
