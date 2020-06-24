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
         * This scenario shows step-by-step the submission of an digital diplom.
         */
        public override async Task RunAsync()
        {
            // 1. The file's bytes must be read by the application and uploaded
            var filePath = "sample-degree.xml";
            var fileName = Path.GetFileName(filePath);
            var file = File.ReadAllBytes(filePath);

            // 1.1 Set the correct mime type for XML files
            var uploadModel = await signerClient.UploadFileAsync(fileName, file, "application/xml");

            // 2. Define the name of the document which will be visible in the application
            var fileUploadModel = new FileUploadModel(uploadModel) { DisplayName = "Digital Degree Sample" };

            // 3. For each participant on the flow, create one instance of ParticipantUserModel.
            var participantUser = new ParticipantUserModel()
            {
                Name = "Jack Bauer",
                Email = "jack.bauer@mailinator.com",
                Identifier = "75502846369"
            };

            // 4. A XML file requires a XadexOptionsModel specifying the signature type. 
            //    Provide the fields that must be signed in the following way:
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

            // 5. Create a FlowActionCreateModel instance for each action (signature or approval) in the flow.
            //    This object is responsible for defining the personal data of the participant and the type of 
            //    action that he will peform on the flow.
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

            // 6. Specify the element that holds the namespace of the issuer.
            var xmlNamespacesModel = new XmlNamespaceModel()
            {
                Prefix = "dip",
                Uri = @"http://portal.mec.gov.br/diplomadigital/arquivos-em-xsd"
            };

            // 7. Send the document create request
            var documentRequest = new CreateDocumentRequest()
            {
                Files = new List<FileUploadModel>() { fileUploadModel },
                FlowActions = new List<FlowActionCreateModel>() 
                {
                    flowActionCreateModelDiplomData,
                    flowActionCreateModelRegisterData
                },
                XmlNamespaces = new List<XmlNamespaceModel>() { xmlNamespacesModel }
            };

            var result = (await signerClient.CreateDocumentAsync(documentRequest)).First();

            System.Console.WriteLine($"Document {result.DocumentId} created");
        }
    }
}
