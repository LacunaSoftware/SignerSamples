﻿using Lacuna.Signer.Api;
using Lacuna.Signer.Api.DocumentMark;
using Lacuna.Signer.Api.Documents;
using Lacuna.Signer.Api.FlowActions;
using Lacuna.Signer.Api.Users;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Console.Scenarios
{
    class CreateDocumentWithPositionedSignatures : Scenario
    {

        /**
         * This scenario demonstrates the creation of a document with 
         * Prepositioned signatures.
         */
        public override async Task RunAsync()
        {
            // 1. The file's bytes must be read by the application and uploaded
            var filePath = "sample.pdf";
            var fileName = Path.GetFileName(filePath);
            var file = File.ReadAllBytes(filePath);
            var uploadModel = await SignerClient.UploadFileAsync(fileName, file, "application/pdf");

            // 2. Define the name of the document which will be visible in the application
            var fileUploadModel = new FileUploadModel(uploadModel) { DisplayName = "Prepositioned signatures" };

            // 3. For each participant on the flow, create one instance of ParticipantUserModel
            var participantUser = new ParticipantUserModel()
            {
                Name = "Jack Bauer",
                Email = "jack.bauer@mailinator.com",
                Identifier = "75502846369"
            };


            // 4. Create a FlowActionCreateModel instance for each action (signature or approval) in the flow.
            //    This object is responsible for defining the personal data of the participant, the type of 
            //    action that he will perform on the flow and the order in which this action will take place
            //    (Step property) and the pre-positioned marks for placing signatures. If the Step property of all action are the same or not specified they 
            //    may be executed at any time
            var flowActionCreateModel = new FlowActionCreateModel()
            {
                Type = FlowActionType.Signer,
                User = participantUser,
                PrePositionedMarks = new List<PrePositionedDocumentMarkModel>
                {
                    new PrePositionedDocumentMarkModel()
                    {
                        Type = DocumentMarkType.SignatureVisualRepresentation, //This is the attribute responsible for defining the Type of signature you are going to use
                        UploadId = fileUploadModel.Id, //Document id
                        TopLeftX = 395.0, //Signature position, in pixels, over the X axis
                        TopLeftY = 560.0, //Signature position, in pixels, over the Y axis
                        Width = 170.0,    //Width of the rectagular where signature will be placed in (It already has a default value)
                        Height = 94.0,    //Height of the rectagular where signature will be placed in (It already has a default value)
                        PageNumber = 1   //Page where the signature wil be placed
                    }
                }
            };


            // 5. Send the document create request
            var documentRequest = new CreateDocumentRequest()
            {
                Files = new List<FileUploadModel>() { fileUploadModel },
                FlowActions = new List<FlowActionCreateModel>()
                {
                    flowActionCreateModel
                }
            };


            var result = (await SignerClient.CreateDocumentAsync(documentRequest)).First();

            System.Console.WriteLine($"Document {result.DocumentId} created");
        }
    }
}
