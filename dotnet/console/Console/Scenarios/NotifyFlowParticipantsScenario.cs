using System.Threading.Tasks;

namespace Console.Scenarios
{
    public class NotifyFlowParticipantsScenario : Scenario
    {
        /**
         * This scenario demonstrates how to notify participants 
         * of the flow.
         */
        public override async Task RunAsync()
        {
            // 1. Get a document Id
            var result = await createDocumentAsync();

            // 2. Get the document details
            var details = await SignerClient.GetDocumentDetailsAsync(result.DocumentId);

            // 3. Notify each participant individually if necessary
            //    Note: Only participants with pending actions are notified.
            foreach (var flowAction in details.FlowActions)
            {
                await SignerClient.SendFlowActionReminderAsync(result.DocumentId, flowAction.Id);
            }
        }
    }
}
