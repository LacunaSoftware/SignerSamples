using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Console.Scenarios
{
    class DeleteDocumentScenario : Scenario
    {
        /**
         * This scenario demonstrates how to delete a document using it's id.
         */
        public override async Task RunAsync()
        {
            //1 - You need a document id
            var document = await CreateDocumentAsync();

            var docId = document.DocumentId;

            //2 - Call the api method to delete the documet and pass the document Id as parameter
            await SignerClient.DeleteDocumentAsync(docId);
            
            
        }
    }
}
