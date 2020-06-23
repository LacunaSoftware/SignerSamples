using Lacuna.Signer.Api;
using Lacuna.Signer.Api.Documents;
using Lacuna.Signer.Api.FlowActions;
using Lacuna.Signer.Api.Users;
using System;
using System.Collections.Generic;
using System.IO;

namespace Console.Scenarios
{
    /**
     * This scenario shows step-by-step the submission of a document
     * to the signer instance where there's a signing rule for it.
     * A signing rule is a modality where multiples participants are
     * assigned to the document but just an arbitrary number of them 
     * are needed to sign in order to complete the flow.
     */
    public class SubmitDocumentWithSigningRuleScenario : Scenario
    {
        public override void Run()
        {
            // 1. The file's bytes must be read by the application and uploaded using the method UploadFileAsync.
            var filePath = "sample.pdf";
            var fileName = Path.GetFileName(filePath);
            var file = File.ReadAllBytes(filePath);
            var uploadModel = signerClient.UploadFileAsync(fileName, file, "application/pdf");

            // 2. Signer's server expects a FileUploadModel's list to create a document.
            var fileUploadModel = new FileUploadModel(uploadModel.Result) { DisplayName = "Signing Rule " + DateTime.UtcNow.ToString() };
            var fileUploadModelList = new List<FileUploadModel>() { fileUploadModel };

            // 3. Foreach participant on the flow, you'll need to create an instance of ParticipantUserModel.
            var participantUserOne = new ParticipantUserModel()
            {
                Name = "Jack Bauer",
                Email = "jack.bauer@mailinator.com",
                Identifier = "75502846369"
            };

            var participantUserTwo = new ParticipantUserModel()
            {
                Name = "James Bond",
                Email = "james.bond@mailinator.com",
                Identifier = "95588148061"
            };

            // 4. Each signing rule requires just one FlowActionCreateModel independent
            //    of the number of participants assigned to it. The participants are added to
            //    it via a list of ParticipantUserModel assigned to the `SignRuleUsers` propertie.
            //    The number of required signatures from this list of participants is represented by
            //    the propertie `NumberRequiredSignatures`.
            var flowActionCreateModelSigningRule = new FlowActionCreateModel()
            {
                Type = FlowActionType.SignRule,
                NumberRequiredSignatures = 1,
                SignRuleUsers = new List<ParticipantUserModel>() { participantUserOne, participantUserTwo }
            };

            // 5. Signer's server expects a FlowActionCreateModel's list to create a document.
            var flowActionCreateModelList = new List<FlowActionCreateModel>() { flowActionCreateModelSigningRule };

            // 6. To create the document request, use the list of FileUploadModel and the list of FlowActionCreateModel.
            var documentRequest = new CreateDocumentRequest()
            {
                Files = fileUploadModelList,
                FlowActions = flowActionCreateModelList
            };
            var documentResults = signerClient.CreateDocumentAsync(documentRequest);

            // 7. To notify the participant:
            foreach (var documentResult in documentResults.Result)
            {
                // 7.1. Extract the information of the flow using the following procedure.
                var details = signerClient.GetDocumentDetailsAsync(documentResult.DocumentId);
                foreach (var flowAction in details.Result.FlowActions)
                {
                    // 7.2. Send notification to the participant.
                    _ = signerClient.SendFlowActionReminderAsync(documentResult.DocumentId, flowAction.Id);
                }
            }
        }
    }
}
