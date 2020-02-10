using Lacuna.Signer.Api;
using Lacuna.Signer.Api.Documents;
using Lacuna.Signer.Api.FlowActions;
using Lacuna.Signer.Api.Users;
using Lacuna.Signer.Client;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Console
{
    class Program
    {
        static void Main(string[] args)
        {
            MainAsync(args).GetAwaiter().GetResult();
        }

        static async Task MainAsync(string[] args)
        {
            var token = "API Sample App|43fc0da834e48b4b840fd6e8c37196cf29f919e5daedba0f1a5ec17406c13a99";
            var client = new SignerClient("https://signer-lac.azurewebsites.net", token);

            //1- Upload file
            //PDF files are signed as PAdES
            //If you need to sign as CAdES, you may set the mime type to null or "application/octet-stream" 
            var file = File.ReadAllBytes("sample.pdf");
            var upload = await client.UploadFileAsync("sample.pdf", file, "application/pdf");

            //2- Create document
            var documentRequest = new CreateDocumentRequest()
            {
                Files = new List<FileUploadModel>()
                {
                    new FileUploadModel(upload)
                    {
                        DisplayName = "Sample document"
                    }
                }, 
                FlowActions = new List<FlowActionCreateModel>()
                {
                    new FlowActionCreateModel()
                    {
                        Type = FlowActionType.Signer, 
                        User = new ParticipantUserModel()
                        {
                            Name = "Jack Bauer", 
                            Email = "jack.bauer@mailinator.com", 
                            Identifier = "75502846369"
                        }
                    }
                }
            };

            var documentIds = await client.CreateDocumentAsync(documentRequest);

            //3- Get document details
            var documentId = documentIds[0];
            var details = await client.GetDocumentDetailsAsync(documentId);

            //to check if document is concluded check the IsConcluded property
            if (details.IsConcluded)
            {

            }

            //to check if individual participants have concluded their actions check the status of the corresponding action
            foreach (var action in details.FlowActions)
            {
                if (action.Status == ActionStatus.Completed)
                {

                }
            }

            //4- Send email reminder to participant
            await client.SendFlowActionReminderAsync(documentId, details.FlowActions[0].Id);

            //5- Download document
            //printer friendly and signed versions may only be downloaded if the document has at least one signature
            var signedDocumentId = new Guid("2e20e453-2bd5-44c5-9be4-fe66d954c619");

            //you may retrieve a pre-authenticated URL (ticket) to download printer friendly, signed and original versions
            var printerFriendlyTicket = await client.GetDocumentDownloadTicketAsync(signedDocumentId, DocumentTicketType.PrinterFriendlyVersion);
            var signedFileTicket = await client.GetDocumentDownloadTicketAsync(signedDocumentId, DocumentTicketType.Signatures);

            //or directly download the files
            var printerFriendlyStream = await client.GetDocumentAsync(signedDocumentId, DocumentTicketType.PrinterFriendlyVersion);
            var signedFileBytes = await client.GetDocumentBytesAsync(signedDocumentId, DocumentTicketType.Signatures);
        }
    }
}
