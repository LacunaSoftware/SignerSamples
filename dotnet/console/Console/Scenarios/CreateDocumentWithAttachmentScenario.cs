using Lacuna.Signer.Api;
using Lacuna.Signer.Api.Attachments;
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
    class CreateDocumentWithAttachmentScenario : Scenario
    {
        /**
         * This scenario demonstrates the creation of a document with Attachment.
         */
        public override async Task RunAsync()
        {
            // 1. The file's bytes must be read by the application and uploaded
            var filePath = "sample.pdf";
            var fileName = Path.GetFileName(filePath);
            var file = File.ReadAllBytes(filePath);
            var uploadModel = await SignerClient.UploadFileAsync(fileName, file, "application/pdf");
            var fileUploadModel = new FileUploadModel(uploadModel) { DisplayName = "One Signer Sample" };
            

            // Repeat the same steps above but now for the attachment
            var attachmentPath = "sample.pdf";
            var attachmentName = Path.GetFileName(attachmentPath);
            var attachment = File.ReadAllBytes(attachmentPath);
            var attachmentUpload = await SignerClient.UploadFileAsync(attachmentName, attachment, "application/pdf");

            // 2. Define the name of the attachment which will be visible in the application using "AttachmentUploadModel"
            var attachmentUploadModel = new AttachmentUploadModel(attachmentUpload) { 
                DisplayName = "One Attachment Sample",
                IsPrivate = true
            };


            // 3. For each participant on the flow, create one instance of ParticipantUserModel
            var participantUser = new ParticipantUserModel()
            {
                Name = "Jack Bauer",
                Email = "jack.bauer@mailinator.com",
                Identifier = "75502846369"
            };

            // 4. Create a FlowActionCreateModel instance for each action (signature or approval) in the flow.
            //    This object is responsible for defining the personal data of the participant and the type of 
            //    action that he will perform on the flow
            var flowActionCreateModel = new FlowActionCreateModel()
            {
                Type = FlowActionType.Signer,
                User = participantUser
            };

            // 5. Send the document create request with whith the attachment attribute
            var documentRequest = new CreateDocumentRequest()
            {
                Files = new List<FileUploadModel>() { fileUploadModel },
                Attachments = new List<AttachmentUploadModel>() { attachmentUploadModel },
                FlowActions = new List<FlowActionCreateModel>() { flowActionCreateModel }
            };
            var result = (await SignerClient.CreateDocumentAsync(documentRequest)).First();

            System.Console.WriteLine($"Document {result.DocumentId} created");
        }
    }
}
