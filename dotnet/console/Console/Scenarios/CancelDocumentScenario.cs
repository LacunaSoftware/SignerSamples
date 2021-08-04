using Lacuna.Signer.Api.Documents;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Console.Scenarios
{
    class CancelDocumentScenario : Scenario
    {
        /**
         * This scenario demonstrates how to cancel a document as an organization application.
         */
        public override async Task RunAsync()
        {
            // 1. You need a document id
            var result = await CreateDocumentAsync();
            var docId = result.DocumentId;

            // 2. Create a cancellation request and give it a reason. Then send the cancellation request
            await SignerClient.CancelDocumentAsync(docId, new CancelDocumentRequest { Reason = "This is a document cancellation" });

        }
    }
}
