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
         * This scenario demonstrates the creation of a digital degree compliant with "PORTARIA Nº 554, DE 11 DE MARÇO DE 2019".
         */
        public override async Task RunAsync()
        {
            // 1. The file's bytes must be read by the application and uploaded
            var filePath = "sample-degree.xml";
            var fileName = Path.GetFileName(filePath);
            var file = File.ReadAllBytes(filePath);
            var uploadModel = await SignerClient.UploadFileAsync(fileName, file, "application/xml");

            // 2. Define the name of the document which will be visible in the application
            var fileUploadModel = new FileUploadModel(uploadModel) { DisplayName = "Digital Degree Sample" };

            // 3. For each participant on the flow, create one instance of ParticipantUserModel
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

            // 4. Specify the element that holds the namespace of the issuer
            var xmlNamespacesModel = new XmlNamespaceModel()
            {
                Prefix = "dip",
                Uri = @"http://portal.mec.gov.br/diplomadigital/arquivos-em-xsd"
            };

            // 5. The fields 'DadosDiploma' and 'DadosRegistro' and the entire XML file must be signed
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

            // 6. Each signature requires its own flow action
            var degreeDataAction = new FlowActionCreateModel()
            {
                Type = FlowActionType.Signer,
                User = participantUserOne,
                XadesOptions = xadesOptionsDegreeData
            };

            var registerDataAction = new FlowActionCreateModel()
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
                    degreeDataAction,
                    registerDataAction,
                    flowActionCreateModelFull 
                },
            };
            var result = (await SignerClient.CreateDocumentAsync(documentRequest)).First();

            System.Console.WriteLine($"Document {result.DocumentId} created");
        }
    }
}
