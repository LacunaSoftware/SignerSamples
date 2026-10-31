using System.IO;
using System.Net.Http;
using System.Threading.Tasks;

namespace Console.Scenarios
{
    /**
     * This scenario creates a document that Signer converts to PDF/A, for long-term archiving, and whose
     * signatures get a timestamp:
     *
     * - convertToPdfA and pdfAProfile: Signer converts the uploaded PDF before the flow starts. The result
     *   then has a new uploadId, not the one you sent: use documentId.
     * - timestampEnabled: overrides the organization's timestamp setting for this document. true timestamps
     *   every signature and false none; leave it out to use the organization's setting. On an instance
     *   without a timestamper, Signer rejects a document that sets it.
     *
     * Lacuna.Signer.Api 2.1.0 has none of these fields, so the scenario creates the document with a direct
     * call to the REST API (CallSignerApiAsync, in Scenario.cs).
     */
    class CreatePdfADocumentWithTimestampScenario : Scenario
    {
        public override async Task RunAsync()
        {
            // 1. Upload the file with the client library, as in the other scenarios.
            var filePath = "sample.pdf";
            var fileName = Path.GetFileName(filePath);
            var file = File.ReadAllBytes(filePath);
            var uploadModel = await SignerClient.UploadFileAsync(fileName, file, "application/pdf");

            // 2. Create the document. The profiles go from PdfA1a to PdfA4f; PdfA2b is a common choice for
            //    archiving.
            var results = await CallSignerApiAsync(HttpMethod.Post, "/api/documents", new
            {
                files = new[]
                {
                    new { id = uploadModel.Id, name = uploadModel.Name, contentType = uploadModel.ContentType, displayName = "PDF/A With Timestamp Sample" }
                },
                flowActions = new[]
                {
                    new
                    {
                        type = "Signer",
                        user = new { name = "Jack Bauer", email = "jack.bauer@mailinator.com", identifier = "75502846369" }
                    }
                },
                convertToPdfA = true,
                pdfAProfile = "PdfA2b",
                timestampEnabled = true
            });
            var documentId = (string)results[0]["documentId"];

            // 3. Read the document back.
            var document = await CallSignerApiAsync(HttpMethod.Get, $"/api/documents/{documentId}");
            System.Console.WriteLine($"Document {documentId}: timestampEnabled = {document["timestampEnabled"]}");
        }
    }
}
