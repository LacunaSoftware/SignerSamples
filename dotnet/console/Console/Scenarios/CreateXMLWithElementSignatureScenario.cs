﻿using Lacuna.Signer.Api;
using Lacuna.Signer.Api.Documents;
using Lacuna.Signer.Api.FlowActions;
using Lacuna.Signer.Api.Users;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Threading.Tasks;

namespace Console.Scenarios
{
    public class CreateXMLWithElementSignatureScenario : Scenario
    {
        /**
         * This scenario shows step by step the creation of a document
         * to the signer instance where the document is a XML file and only
         * a specific element of the document must be signed.
         */
        public override async Task RunAsync()
        {
            // 1. The file's bytes must be read by the application and uploaded using the method UploadFileAsync.
            var filePath = "sample.xml";
            var fileName = Path.GetFileName(filePath);
            var file = File.ReadAllBytes(filePath);

            // 1.1 The mimeType for a xml file is "application/xml".
            var uploadModel = await signerClient.UploadFileAsync(fileName, file, "application/xml");

            // 2. Signer's server expects a FileUploadModel's list to create a document.
            var fileUploadModel = new FileUploadModel(uploadModel) { DisplayName = "XML Element Sign Sample" };

            // 3. Foreach participant on the flow, you'll need to create an instance of ParticipantUserModel.
            var participantUser = new ParticipantUserModel()
            {
                Name = "Jack Bauer",
                Email = "jack.bauer@mailinator.com",
                Identifier = "75502846369"
            };

            // 4. For a XML file it's necessary to provide for the FlowActionCreateModel a XadexOptionsModel
            //    specifying the signature type. In this case, it's necessary to specify the type of SignartureType
            //    for XmlElement, specify the type of the element (We are using Id here) and the value of the identifier
            //    which must be previously discovered.
            var xadesOptionsModel = new XadesOptionsModel()
            {
                SignatureType = XadesSignatureTypes.XmlElement,
                ElementToSignIdentifierType = XadesElementIdentifierTypes.Id,
                ElementToSignIdentifier = "NFe35141214314050000662550010001084271182362300"
            };

            // 5. You'll need to create a FlowActionCreateModel's instance foreach ParticipantUserModel
            //    created in the previous step. The FlowActionCreateModel is responsible for holding
            //    the personal data of the participant and the type of action that it will peform on the flow.
            //    Also, it's necessary to instantiate the propertie 'XadexOptions' with the previously created instance XadexOptionsModel.
            var flowActionCreateModel = new FlowActionCreateModel()
            {
                Type = FlowActionType.Signer,
                User = participantUser,
                XadesOptions = xadesOptionsModel
            };

            // 6. Send the document create request
            var documentRequest = new CreateDocumentRequest()
            {
                Files = new List<FileUploadModel>() { fileUploadModel },
                FlowActions = new List<FlowActionCreateModel>() { flowActionCreateModel }
            };
            var result = (await signerClient.CreateDocumentAsync(documentRequest)).First();

            System.Console.WriteLine($"Document {result.DocumentId} created");
        }
    }
}