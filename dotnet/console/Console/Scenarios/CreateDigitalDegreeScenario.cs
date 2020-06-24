using Lacuna.Signer.Api;
using Lacuna.Signer.Api.Documents;
using Lacuna.Signer.Api.FlowActions;
using Lacuna.Signer.Api.Users;
using System;
using System.Collections.Generic;
using System.IO;

namespace Console.Scenarios
{
    public class CreateDigitalDegreeScenario : Scenario
    {
        /**
         * This scenario shows step-by-step the submission of an digital diplom.
         */
        public override void Run()
        {
            // 1. The file's bytes must be read by the application and uploaded using the method UploadFileAsync.
            var filePath = "sample-diplom.xml";
            var fileName = Path.GetFileName(filePath);
            var file = File.ReadAllBytes(filePath);

            // 1.1 The mimeType for a xml file is "application/xml".
            var uploadModel = signerClient.UploadFileAsync(fileName, file, "application/xml");

            // 2. Signer's server expects a FileUploadModel's list to create a document.
            var fileUploadModel = new FileUploadModel(uploadModel.Result) { DisplayName = "Diplom " + DateTime.UtcNow.ToString() };
            var fileUploadModelList = new List<FileUploadModel>() { fileUploadModel };

            // 3. Foreach participant on the flow, you'll need to create an instance of ParticipantUserModel.
            var participantUser = new ParticipantUserModel()
            {
                Name = "Jack Bauer",
                Email = "jack.bauer@mailinator.com",
                Identifier = "75502846369"
            };

            // 4. For a XML file it's necessary to provide for the FlowActionCreateModel a XadexOptionsModel
            //    specifying the signature type. In this case, it's necessary to provide the fields that must be
            //    signed in the following way:
            var xadesOptionsDiplomData = new XadesOptionsModel
            {
                SignatureType = XadesSignatureTypes.XmlElement,
                ElementToSignIdentifierType = XadesElementIdentifierTypes.XPath,
                ElementToSignIdentifier = @"//dip:DadosDiploma",
                InsertionOption = XadesInsertionOptions.AppendChild
            };

            var xadesOptionsModelRegisterData = new XadesOptionsModel
            {
                SignatureType = XadesSignatureTypes.XmlElement,
                ElementToSignIdentifierType = XadesElementIdentifierTypes.XPath,
                ElementToSignIdentifier = @"//dip:DadosRegistro",
                InsertionOption = XadesInsertionOptions.AppendChild
            };

            // 5. You'll need to create two FlowActionCreateModel's instance foreach ParticipantUserModel
            //    one for signing the register's data and one for the diplom's data.
            var flowActionCreateModelDiplomData = new FlowActionCreateModel()
            {
                Type = FlowActionType.Signer,
                User = participantUser,
                XadesOptions = xadesOptionsDiplomData
            };

            var flowActionCreateModelRegisterData = new FlowActionCreateModel()
            {
                Type = FlowActionType.Signer,
                User = participantUser,
                XadesOptions = xadesOptionsModelRegisterData
            };

            // 6. Signer's server expects a FlowActionCreateModel's list to create a document.
            var flowActionCreateModelList = new List<FlowActionCreateModel>() { 
                flowActionCreateModelDiplomData,
                flowActionCreateModelRegisterData
            };

            // 7. For a diplom, it's necessary to provide an instance holding the namespace of
            //    the issuer.
            var xmlNamespacesModel = new XmlNamespaceModel()
            {
                Prefix = "dip",
                Uri = @"http://portal.mec.gov.br/diplomadigital/arquivos-em-xsd"
            };
            var xmlNamespacesModelList = new List<XmlNamespaceModel>() { xmlNamespacesModel };

            // 8. To create the document request, use the list of FileUploadModel, the list of FlowActionCreateModel 
            //    and the namespace model.
            var documentRequest = new CreateDocumentRequest()
            {
                Files = fileUploadModelList,
                FlowActions = flowActionCreateModelList,
                XmlNamespaces = xmlNamespacesModelList
            };
            var documentResults = signerClient.CreateDocumentAsync(documentRequest);

            // 9. To notify the participant:
            foreach (var documentResult in documentResults.Result)
            {
                // 9.1. Extract the information of the flow using the following procedure.
                var details = signerClient.GetDocumentDetailsAsync(documentResult.DocumentId);
                foreach (var flowAction in details.Result.FlowActions)
                {
                    // 9.2. Send notification to the participant.
                    _ = signerClient.SendFlowActionReminderAsync(documentResult.DocumentId, flowAction.Id);
                }
            }
        }
    }
}
