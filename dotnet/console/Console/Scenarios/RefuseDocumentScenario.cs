using Lacuna.Signer.Api.Refusal;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Console.Scenarios
{
    class RefuseDocumentScenario : Scenario
    {
        /**
         * This scenario demonstrates how to refuse a document as an organization application.
         */
        public override async Task RunAsync()
        {
            // 1. You need a document id
            var result = await CreateDocumentAsync();
            var docId = result.DocumentId;

            // 2. Create a refusal request and give it a reason. Then send the refusal request
            await SignerClient.RefuseDocumentAsync(docId, new RefusalRequest { Reason = "This is a document refusal" });
         
        }
    }
}
