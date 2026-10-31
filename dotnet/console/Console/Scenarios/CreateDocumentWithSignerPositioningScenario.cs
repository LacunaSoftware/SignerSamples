using System.IO;
using System.Net.Http;
using System.Threading.Tasks;

namespace Console.Scenarios
{
    /**
     * This scenario sets, for one document, two settings that otherwise come from the organization:
     *
     * - signerPositioningEnabled: whether the signer may place their own signature on the page.
     * - automaticSignaturePositioning: what Signer does with a signature that nobody placed: Footnote puts
     *   it in the page footer, SignatureSheet on a signature sheet at the end of the PDF, and Disabled
     *   leaves it off. Disabled can't be combined with signerPositioningEnabled = false.
     *
     * It sets them when it creates the document, then changes them with PUT /signer-positioning, which
     * works until a participant signs or approves. A null value takes the organization's current
     * setting, and the document keeps it even if the organization changes later.
     *
     * Lacuna.Signer.Api 2.1.0 has neither setting, so the scenario calls the REST API directly
     * (CallSignerApiAsync, in Scenario.cs).
     */
    class CreateDocumentWithSignerPositioningScenario : Scenario
    {
        public override async Task RunAsync()
        {
            // 1. Upload the file with the client library, as in the other scenarios.
            var filePath = "sample.pdf";
            var fileName = Path.GetFileName(filePath);
            var file = File.ReadAllBytes(filePath);
            var uploadModel = await SignerClient.UploadFileAsync(fileName, file, "application/pdf");

            // 2. Create the document: the signer places their signature, and a signature nobody placed goes
            //    on a signature sheet.
            var results = await CallSignerApiAsync(HttpMethod.Post, "/api/documents", new
            {
                files = new[]
                {
                    new { id = uploadModel.Id, name = uploadModel.Name, contentType = uploadModel.ContentType, displayName = "Signer Positioning Sample" }
                },
                flowActions = new[]
                {
                    new
                    {
                        type = "Signer",
                        user = new { name = "Jack Bauer", email = "jack.bauer@mailinator.com", identifier = "75502846369" }
                    }
                },
                signerPositioningEnabled = true,
                automaticSignaturePositioning = "SignatureSheet"
            });
            var documentId = (string)results[0]["documentId"];
            await PrintSettingsAsync(documentId);

            // 3. Before the first signature, change them: the signer can't move the signature any more, and
            //    an unplaced one goes in a footnote.
            await CallSignerApiAsync(HttpMethod.Put, $"/api/documents/{documentId}/signer-positioning", new
            {
                signerPositioningEnabled = false,
                automaticSignaturePositioning = "Footnote"
            });
            await PrintSettingsAsync(documentId);
        }

        private async Task PrintSettingsAsync(string documentId)
        {
            var document = await CallSignerApiAsync(HttpMethod.Get, $"/api/documents/{documentId}");
            System.Console.WriteLine($"Document {documentId}: signerPositioningEnabled = {document["signerPositioningEnabled"]}, automaticSignaturePositioning = {document["automaticSignaturePositioning"]}");
        }
    }
}
