using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Console.Scenarios
{
    public class NotifyFlowParticipantsScenario : Scenario
    {
        /**
         * This scenario shows the proccess of notifying the participants of the flow.
         */
        public override async Task RunAsync()
        {
            // 1. It's necessary to have a CreateDocumentResult instance.
            var result = await createDocumentAsync();

            // 2. Obtain the document details with it's Id.
            var details = await signerClient.GetDocumentDetailsAsync(result.DocumentId);

            // 3. Notify the participants. 
            //    Note: Only participants with pending actions are notified.
            foreach (var flowAction in details.FlowActions)
            {
                _ = signerClient.SendFlowActionReminderAsync(result.DocumentId, flowAction.Id);
            }
        }
    }
}
