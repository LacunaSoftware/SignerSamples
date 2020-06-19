﻿using Lacuna.Signer.Api;
using Lacuna.Signer.Api.Documents;
using Lacuna.Signer.Api.FlowActions;
using Lacuna.Signer.Api.Users;
using System;
using System.Collections.Generic;
using System.IO;

namespace Console.Scenarios
{
    public class SubmitFullSignXMLDocumentScenario : Scenario
    {
        public override async void Run()
        {
            var filePath = "sample.pdf";
            var fileName = Path.GetFileName(filePath);
            var file = File.ReadAllBytes(filePath);

            var uploadModel = signerClient.UploadFileAsync(fileName, file, "application/xml");

            var fileUploadModel = new FileUploadModel(uploadModel.Result) { DisplayName = "XML Full Sign " + DateTime.UtcNow.ToString() };
            var fileUploadModelList = new List<FileUploadModel>() { fileUploadModel };

            var participantUser = new ParticipantUserModel()
            {
                Name = "Jack Bauer",
                Email = "jack.bauer@mailinator.com",
                Identifier = "75502846369"
            };

            var xadesOptionsModel = new XadesOptionsModel()
            {
                SignatureType = XadesSignatureTypes.FullXml
            };

            var flowActionCreateModel = new FlowActionCreateModel()
            {
                Type = FlowActionType.Signer,
                User = participantUser,
                XadesOptions = xadesOptionsModel
            };

            var flowActionCreateModelList = new List<FlowActionCreateModel>() { flowActionCreateModel };

            var documentRequest = new CreateDocumentRequest()
            {
                Files = fileUploadModelList,
                FlowActions = flowActionCreateModelList
            };
            var documentResult = signerClient.CreateDocumentAsync(documentRequest);

            var documentId = documentResult.Result[0].DocumentId;
            var details = signerClient.GetDocumentDetailsAsync(documentId);
            var flowAction = details.Result.FlowActions[0];
            await signerClient.SendFlowActionReminderAsync(documentId, flowAction.Id);
        }
    }
}
