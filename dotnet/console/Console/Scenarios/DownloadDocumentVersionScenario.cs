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
         * This scenario demonstrates how to download different document versions: the original file,
         * the signed file (Signatures) and the printer-friendly version.
         */

        // Signatures and PrinterFriendlyVersion exist only after at least one participant has signed.
        // To download them, set this to the id of a signed document of your organization, for instance
        // one whose status is Concluded. While it is null, the scenario creates a new document, which
        // nobody has signed yet, and downloads only its Original version.
        private static readonly Guid? SignedDocumentId = null;

        public override async Task RunAsync()
        {
            // 1. Get a document Id
            var documentId = SignedDocumentId ?? (await CreateDocumentAsync()).DocumentId;

            //2. You can get a ticket to a specific version of the document. The ticket is a temporary URL (valid for 1 hour,
            //   no authentication needed) that allows you to download that version. Anyone with the URL can download it.
            var ticketDownload = await SignerClient.GetDocumentDownloadTicketAsync(documentId, DocumentTicketType.Original);

            // 3. Get the document by passing its Id and the version type.
            // Be sure to select the exact DocumentDownloadTypes to download the type of document you want.
            // Original: the file uploaded when the document was created. It is always available.
            var original = await SignerClient.GetDocumentContentAsync(documentId, DocumentDownloadTypes.Original);
            this.SaveFileStream("downloaded-original.pdf", original);

            // 4. You can also get the bytes directly instead of a Stream for a specific version type of the document
            var documentVersionBytes = await SignerClient.GetDocumentBytesAsync(documentId, DocumentTicketType.Original);

            // 5. For a document that nobody has signed yet, Signatures and PrinterFriendlyVersion fail with
            //    422 and the code DocumentHasNoSignatures (SignerApiException). Read the document first.
            var details = await SignerClient.GetDocumentDetailsAsync(documentId);
            if (!details.HasSignature)
            {
                System.Console.WriteLine($"Document {documentId} has no signatures yet, so only its Original version was downloaded.");
                return;
            }

            // 6. Signatures: the signed file. For a PDF signed with PAdES (the default) it is the PDF with the signatures
            //    embedded; for a CAdES signature it is a .p7s file. It holds the signatures collected so far: the document
            //    is final only when its status is Concluded. Canceled, refused and expired documents download only as
            //    Original and PrinterFriendlyVersion.
            if (details.Status == DocumentStatus.Canceled || details.Status == DocumentStatus.Refused || details.Status == DocumentStatus.Expired)
            {
                System.Console.WriteLine($"Document {documentId} is {details.Status}, so its signed file cannot be downloaded.");
            }
            else
            {
                var signed = await SignerClient.GetDocumentContentAsync(documentId, DocumentDownloadTypes.Signatures);
                this.SaveFileStream("downloaded-signed.pdf", signed);
            }

            // 7. PrinterFriendlyVersion: a PDF with the signature marks and a signatures manifest appended.
            var printerFriendly = await SignerClient.GetDocumentContentAsync(documentId, DocumentDownloadTypes.PrinterFriendlyVersion);
            this.SaveFileStream("downloaded-printer-friendly.pdf", printerFriendly);
        }


        private void SaveFileStream(String path, Stream stream)
        {
            using (stream)
            using (var fileStream = new FileStream(path, FileMode.Create, FileAccess.Write))
            {
                stream.CopyTo(fileStream);
            }
            System.Console.WriteLine($"Saved {Path.GetFullPath(path)}");
        }

    }
}
