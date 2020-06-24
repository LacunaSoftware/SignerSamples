using Lacuna.Signer.Api;
using Lacuna.Signer.Api.Documents;
using Lacuna.Signer.Api.FlowActions;
using Lacuna.Signer.Api.Users;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Threading.Tasks;

namespace Console.Scenarios
{
    public class CreateDigitalDegreeScenario : Scenario
    {
        /**
         * This scenario shows step-by-step the submission of an digital degree.
         */
        public override async Task RunAsync()
        {
            // 1. The file's bytes must be read by the application and uploaded using the method UploadFileAsync.
            var filePath = "sample-degree.xml";
            var fileName = Path.GetFileName(filePath);
            var file = File.ReadAllBytes(filePath);

            // 1.1 The mimeType for a xml file is "application/xml".
            var uploadModel = signerClient.UploadFileAsync(fileName, file, "application/xml");

            // 2. Signer's server expects a FileUploadModel's list to create a document.
            var fileUploadModel = new FileUploadModel(uploadModel.Result) { DisplayName = "Degree " + DateTime.UtcNow.ToString() };
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

            var ParticipantUserThree = new ParticipantUserModel()
            {
                Name = "Gary Eggsy",
                Email = "gary.eggsy@mailinator.com",
                Identifier = "87657257008"
            };

            // 4. For a XML file it's necessary to provide for the FlowActionCreateModel a XadexOptionsModel
            //    specifying the signature type. In this case, it's necessary to provide the fields that must be
            //    signed in the following way:
            var xadesOptionsDegreeData = new XadesOptionsModel()
            {
                SignatureType = XadesSignatureTypes.XmlElement,
                ElementToSignIdentifierType = XadesElementIdentifierTypes.XPath,
                ElementToSignIdentifier = @"//dip:DadosDiploma",
                InsertionOption = XadesInsertionOptions.AppendChild
            };

            var xadesOptionsModelRegisterData = new XadesOptionsModel()
            {
                SignatureType = XadesSignatureTypes.XmlElement,
                ElementToSignIdentifierType = XadesElementIdentifierTypes.XPath,
                ElementToSignIdentifier = @"//dip:DadosRegistro",
                InsertionOption = XadesInsertionOptions.AppendChild
            };

            var xadesOptionsModelFull = new XadesOptionsModel()
            {
                SignatureType = XadesSignatureTypes.FullXml
            };

            // 5. You'll need to create two FlowActionCreateModel's instance foreach ParticipantUserModel
            //    one for signing the register's data and one for the degree's data.
            var flowActionCreateModelDegreeData = new FlowActionCreateModel()
            {
                Type = FlowActionType.Signer,
                User = participantUserOne,
                XadesOptions = xadesOptionsDegreeData
            };

            var flowActionCreateModelRegisterData = new FlowActionCreateModel()
            {
                Type = FlowActionType.Signer,
                User = participantUserTwo,
                XadesOptions = xadesOptionsModelRegisterData
            };

            var flowActionCreateModelFull = new FlowActionCreateModel()
            {
                Type = FlowActionType.Signer,
                User = ParticipantUserThree,
                XadesOptions = xadesOptionsModelFull
            };

            // 6. Signer's server expects a FlowActionCreateModel's list to create a document.
            var flowActionCreateModelList = new List<FlowActionCreateModel>() {
                flowActionCreateModelDegreeData,
                flowActionCreateModelRegisterData,
                flowActionCreateModelFull
            };

            // 7. For a degree, it's necessary to provide an instance holding the namespace of
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
            var result = (await signerClient.CreateDocumentAsync(documentRequest)).First();

            System.Console.WriteLine($"Document {result.DocumentId} created");
        }
    }
}
