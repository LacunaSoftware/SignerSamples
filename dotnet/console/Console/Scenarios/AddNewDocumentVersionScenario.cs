using Lacuna.Signer.Api;
using Lacuna.Signer.Api.Documents;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Console.Scenarios
{
    class AddNewDocumentVersionScenario : Scenario
    {
        /**
         * This scenario demonstrates how to add a new document version to a existing document (the flow will be reset).
         */
        public override async Task RunAsync()
        {
            //1 - You need a document id
            var document = await CreateDocumentAsync();
            var docId = document.DocumentId;

            // 2. The file's bytes must be read by the application and uploaded
            var filePath = "sample.pdf";
            var fileName = Path.GetFileName(filePath);
            var file = File.ReadAllBytes(filePath);
            var uploadModel = await SignerClient.UploadFileAsync(fileName, file, "application/pdf");

            // 3. Define the name of the document which will be visible in the application
            var fileUploadModel = new FileUploadModel(uploadModel) { DisplayName = "Add New Document Version Sample" };

            // 4. Send the new version request
            var documentAddVersionRequest = new DocumentAddVersionRequest() { File = fileUploadModel };
            await SignerClient.AddDocumentVersionAsync(docId , documentAddVersionRequest);

        }
    }
}
