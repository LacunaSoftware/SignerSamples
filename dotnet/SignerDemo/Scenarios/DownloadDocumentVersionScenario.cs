using Lacuna.Signer.Api;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SignerDemo.Scenarios
{
    class DownloadDocumentVersionScenario : Scenario
    {
        /**
         * This scenario demonstrates how to download different document versions 
         */

        public override async Task RunAsync()
        {
            // 1. Get a document Id
            var result = await CreateDocumentAsync();

	        
            //2. You can get a ticket to a specific version of the document. The ticket is a temporary URL that allows you to download that version. 
            var ticketDownload = await SignerClient.GetDocumentDownloadTicketAsync(result.DocumentId, DocumentTicketType.Original);


            // 3. Get the document by passing it's Id and the Ticket type
            // Be sure to select the exact DocumentTicketType to download the type of document you want. 
            // Chek the available types by ispecting DocumentTicketType's ENUM.
            var documentVersion = await SignerClient.GetDocumentContentAsync(result.DocumentId, DocumentDownloadTypes.Original);

            this.SaveFileStream("downloadversionExample.pdf", documentVersion);

            // 4. You can also get the bytes directly instead of a Stream for a specific version type of the document
            var documentVersionBytes = await SignerClient.GetDocumentBytesAsync(result.DocumentId, DocumentTicketType.Signatures);
            

        }


        private void SaveFileStream(String path, Stream stream)
        {
            var fileStream = new FileStream(path, FileMode.Create, FileAccess.Write);
            stream.CopyTo(fileStream);
            fileStream.Dispose();
        }

    }
}
