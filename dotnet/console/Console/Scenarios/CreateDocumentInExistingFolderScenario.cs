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

namespace Console.Scenarios
{
    public class CreateDocumentInExistingFolderScenario : Scenario
    {
        /**
         * This scenario shows step by step the creation of a document in a existing folder.
         */
        public CreateDocumentInExistingFolderScenario() : base()
        {
            // 0. It's necessary to have an existing folder.
            _ = signerClient.CreateFolderAsync(new FolderCreateRequest() { Name = "ExistingFolderName" });
        }

        /**
         * This scenario shows step by step the creation of a document
         * into an already existing folder.
         */
        public override async Task RunAsync()
        {
            // 1. The file's bytes must be read by the application and uploaded
            var filePath = "sample.pdf";
            var fileName = Path.GetFileName(filePath);
            var file = File.ReadAllBytes(filePath);
            var uploadModel = await signerClient.UploadFileAsync(fileName, file, "application/pdf");

            // 2. Define the name of the document which will be visible in the application
            var fileUploadModel = new FileUploadModel(uploadModel) { DisplayName = "Document in Folder Sample" };

            // 3. For each participant on the flow, create one instance of ParticipantUserModel.
            var participantUser = new ParticipantUserModel()
            {
                Name = "Jack Bauer",
                Email = "jack.bauer@mailinator.com",
                Identifier = "75502846369"
            };

            // 4. Create a FlowActionCreateModel instance for each action (signature or approval) in the flow.
            //    This object is responsible for defining the personal data of the participant and the type of 
            //    action that he will peform on the flow.
            var flowActionCreateModel = new FlowActionCreateModel()
            {
                Type = FlowActionType.Signer,
                User = participantUser
            };

            // 5. You'll need to retrieve the folder details.
            var paginatedSearchParams = new PaginatedSearchParams() { Q = "ExistingFolderName" };
            var paginatedSearchResult = await signerClient.ListFoldersPaginatedAsync(paginatedSearchParams, null);
            var folderInfoModel = paginatedSearchResult.Items.First();

            // 6. Send the document create request .Set the FolderId property to insert the document in a folder.
            var documentRequest = new CreateDocumentRequest()
            {
                Files = new List<FileUploadModel>() { fileUploadModel },
                FlowActions = new List<FlowActionCreateModel>() { flowActionCreateModel },
                FolderId = folderInfoModel.Id
            };
            var result = (await signerClient.CreateDocumentAsync(documentRequest)).First();

            System.Console.WriteLine($"Document {result.DocumentId} created");
        }
    }
}
