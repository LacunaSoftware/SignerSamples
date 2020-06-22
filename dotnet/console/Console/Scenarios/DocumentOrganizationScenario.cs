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
    public class DocumentOrganizationScenario : Scenario
    {
        public override void Run()
        {
            // 1. The file's bytes must be read by the application and uploaded using the method UploadFileAsync.
            var filePath = "sample.pdf";
            var fileName = Path.GetFileName(filePath);
            var file = File.ReadAllBytes(filePath);
            var uploadModel = signerClient.UploadFileAsync(fileName, file, "application/pdf");

            // 2. Signer's server expects a FileUploadModel's list to create a document.
            var fileUploadModel = new FileUploadModel(uploadModel.Result) { DisplayName = "Doc in Folder " + DateTime.UtcNow.ToString() };
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

            // 6. You'll need to request the creation of a folder or get an existing one.
            var folderCreateRequest = new FolderCreateRequest()
            {
                Name = "Folder " + DateTime.UtcNow.ToString()
            };
            var folderInfoModel = signerClient.CreateFolderAsync(folderCreateRequest);

            // 7. To create the document request, use the list of FileUploadModel and the list of FlowActionCreateModel.
            //    Also, it's necessary to provide the id of the folder where you wish to save the document.
            //    It's also possible to create a brand new folder by lefting the `FolderId` property as null and assigning 
            //    the occult propertie `NewFolderName` with the desired name for the folder.
            var documentRequest = new CreateDocumentRequest()
            {
                Files = fileUploadModelList,
                FlowActions = flowActionCreateModelList,
                FolderId = folderInfoModel.Result.Id
            };
            var documentResults = signerClient.CreateDocumentAsync(documentRequest).Result;
        }
    }
}
