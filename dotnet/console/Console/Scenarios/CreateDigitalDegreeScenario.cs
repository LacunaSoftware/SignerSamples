using Lacuna.Signer.Api;
using Lacuna.Signer.Api.Documents;
using Lacuna.Signer.Api.FlowActions;
using Lacuna.Signer.Api.Users;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Threading.Tasks;

namespace Console.Scenarios
{
    public class CreateDigitalDegreeScenario : Scenario
    {
        /**
         * This scenario shows step by step the creation of an digital degree in the instance.
         */
        public override async Task RunAsync()
        {
            // 1. The file's bytes must be read by the application and uploaded
            var filePath = "sample-degree.xml";
            var fileName = Path.GetFileName(filePath);
            var file = File.ReadAllBytes(filePath);
            var uploadModel = signerClient.UploadFileAsync(fileName, file, "application/xml");

            // 2. Define the name of the document which will be visible in the application
            var fileUploadModel = new FileUploadModel(uploadModel.Result) { DisplayName = "Digital Degree" };

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

            // 4. For a degree, it's necessary to provide an instance holding the namespace of the issuer.
            var xmlNamespacesModel = new XmlNamespaceModel()
            {
                Prefix = "dip",
                Uri = @"http://portal.mec.gov.br/diplomadigital/arquivos-em-xsd"
            };

            // 5. For signing a digital degree, it's necessary to sign the fields 'DadosDiploma' and 'DadosRegistro',
            //    also the entire xml file.
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

            // 6. Each signature requires a unique instance of flow action.
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

            // 7. Send the document create request
            var documentRequest = new CreateDocumentRequest()
            {
                Files = new List<FileUploadModel>() { fileUploadModel },
                XmlNamespaces = new List<XmlNamespaceModel>() { xmlNamespacesModel },
                FlowActions = new List<FlowActionCreateModel>() 
                {
                    flowActionCreateModelDegreeData,
                    flowActionCreateModelRegisterData,
                    flowActionCreateModelFull 
                },
            };
            var result = (await signerClient.CreateDocumentAsync(documentRequest)).First();

            System.Console.WriteLine($"Document {result.DocumentId} created");
        }
    }
}
