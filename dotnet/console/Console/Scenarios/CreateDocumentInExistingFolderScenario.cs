using Lacuna.Signer.Api;
using Lacuna.Signer.Api.Documents;
using Lacuna.Signer.Api.FlowActions;
using Lacuna.Signer.Api.Users;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Threading.Tasks;
using Lacuna.Signer.Api.Folders;
using Lacuna.Spa.Api;
using System;

namespace Console.Scenarios
{
    public class CreateDocumentInExistingFolderScenario : Scenario
    {
        /**
         * This scenario demonstrates the creation of a document into an existing folder.
         */
        public override async Task RunAsync()
        {
            // 1. The file's bytes must be read by the application and uploaded
            var filePath = "sample.pdf";
            var fileName = Path.GetFileName(filePath);
            var file = File.ReadAllBytes(filePath);
            var uploadModel = await SignerClient.UploadFileAsync(fileName, file, "application/pdf");

            // 2. Define the name of the document which will be visible in the application
            var fileUploadModel = new FileUploadModel(uploadModel) { DisplayName = "Document in Folder Sample" };

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

            // 5. Search a folder by it's name
            var paginatedSearchParams = new PaginatedSearchParams() { Q = "Sample Folder" };
            var paginatedSearchResult = await SignerClient.ListFoldersPaginatedAsync(paginatedSearchParams, null);
            var folder = paginatedSearchResult.Items.FirstOrDefault();

            if (folder == null)
            {
                throw new Exception("Folder was not found");
            }

            // 6. Send the document create request setting the FolderId property
            var documentRequest = new CreateDocumentRequest()
            {
                Files = new List<FileUploadModel>() { fileUploadModel },
                FlowActions = new List<FlowActionCreateModel>() { flowActionCreateModel },
                FolderId = folder.Id
            };
            var result = (await SignerClient.CreateDocumentAsync(documentRequest)).First();

            System.Console.WriteLine($"Document {result.DocumentId} created");
        }
    }
}
