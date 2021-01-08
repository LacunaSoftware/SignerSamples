using Lacuna.Signer.Api;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Console.Scenarios
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

            // 2. You can directly get an  especific document ticket 
            var ticketDownload = await SignerClient.GetDocumentDownloadTicketAsync(result.DocumentId, DocumentTicketType.Original);

            // 3. Get the document by passing it's Id and the Ticket type (This is a direct download and It already calls the method above)
            // Be sure to select the exact DocumentTicketType to download the type of document you want. 
            // Chek the available types by ispecting DocumentTicketType's ENUM.
            var documentVersion = await SignerClient.GetDocumentAsync(result.DocumentId, DocumentTicketType.Original);

            // 4. You can also download a specific version type of the document encoded in Base 64 format.
            var documentVersionBytes = await SignerClient.GetDocumentBytesAsync(result.DocumentId, DocumentTicketType.Signatures);


            this.SaveFileStream("downloadversionExample.pdf", documentVersion);

            /**
             * NOTE: 
             * There is deprecated method called GetDocumentContentAsync() that has the same return as the GetDocumentAsync() method.
             * We recomend the use of the method applied at the example above.
             */

        }


        private void SaveFileStream(String path, Stream stream)
        {
            var fileStream = new FileStream(path, FileMode.Create, FileAccess.Write);
            stream.CopyTo(fileStream);
            fileStream.Dispose();
        }

    }
}
