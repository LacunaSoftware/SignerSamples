using Lacuna.Signer.Api;
using Lacuna.Signer.Api.Documents;
using Lacuna.Signer.Api.FlowActions;
using Lacuna.Signer.Api.Folders;
using Lacuna.Signer.Api.Users;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Console.Scenarios
{
    public class CreateDocumentInFolderScenario : Scenario
    {
        /**
         * This scenario shows step-by-step the submission of a document
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

            // 5. You'll need to request the creation of a folder or get an existing one.
            var folderCreateRequest = new FolderCreateRequest()
            {
                Name = "Folder " + DateTime.UtcNow.ToString()
            };
            var folderInfoModel = await signerClient.CreateFolderAsync(folderCreateRequest);

            // 7. Send the document create request and provide the id of the folder where you wish to save the document.
            //    It's also possible to create a brand new folder by lefting the `FolderId` property as null and assigning 
            //    the property `NewFolderName` with the desired name for the folder.
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
