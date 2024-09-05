using Lacuna.Signer.Api.Documents;
using Lacuna.Signer.Api.FlowActions;
using Lacuna.Signer.Api.Users;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Linq.Expressions;
using System.Text;
using System.Threading.Tasks;

namespace Console.Scenarios {
    internal class AddSignerToExistingDocumentScenario : Scenario {
        public override async Task RunAsync() {
            // 0. You need a document id
            var result = await CreateDocumentAsync();
            var docId = result.DocumentId;

            // 1.Get the document details
            var details = await SignerClient.GetDocumentDetailsAsync(docId);

            // 2. Input the ongoing flowActionId to be able to change previously defined FlowActions
            var flowActions = details.FlowActions;

            // 3. For each participant on the new flow, create one instance of ParticipantUserModel
            var user = new ParticipantUserModel() {
                Name = "Anakin Skywalker",
                Email = "anakin.skywalker@mailnator.com",
                Identifier = "75502846369"
            };

            // 4. Create a FlowActionCreateModel instance for each action (signature or approval) in the flow.
            //    This object is responsible for defining the personal data of the participant and the type of
            //    action that he will perform on the flow
            var flowActionCreateModel = new FlowActionCreateModel() {
                Type = Lacuna.Signer.Api.FlowActionType.Signer,
                User = user,
            };

            // 5. The new flow action step must be greater or equal to the current pending step
            flowActionCreateModel.Step = flowActions.Count() + 1;

            // 5.5 Create list for flowAction
            var flowActionList = new List<FlowActionCreateModel> { flowActionCreateModel };

            // 6. Prepare the request
            var documentEditFlowRequest = new DocumentFlowEditRequest() {
                AddedFlowActions = flowActionList
            };

            // 7. Pass the parameters to the editflow function to perform the request
            await SignerClient.EditDocumentFlowAsync(docId, documentEditFlowRequest);

            // 8. Send a reminder to the new participants of the document flow
            await RemindFlowUsersWithPendingActions(docId);

        }

        private async Task RemindFlowUsersWithPendingActions(Guid docId) {
            var details = await SignerClient.GetDocumentDetailsAsync(docId);
            foreach (var flowAction in details.FlowActions) {
                if (flowAction.Status == Lacuna.Signer.Api.ActionStatus.Pending) {
                    await SignerClient.SendFlowActionReminderAsync(docId, flowAction.Id);
                }
            }
        }
    }
}
