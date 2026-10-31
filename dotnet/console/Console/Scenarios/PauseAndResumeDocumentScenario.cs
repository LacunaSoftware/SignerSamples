using System.Net.Http;
using System.Threading.Tasks;

namespace Console.Scenarios
{
    /**
     * This scenario pauses a pending document and then resumes it. While a document is paused, nobody can
     * sign, approve or refuse it, Signer sends no reminders and the document doesn't expire; you can still
     * cancel it. Resuming lets the flow continue where it stopped. Pausing and resuming each e-mail the
     * pending participants and send a webhook event, DocumentPaused or DocumentResumed.
     *
     * Lacuna.Signer.Client has no pause or resume methods, so the scenario calls the REST API
     * directly (CallSignerApiAsync, in Scenario.cs). It reads the document the same way:
     * GetDocumentDetailsAsync throws on a paused document, because the DocumentStatus enum of
     * Lacuna.Signer.Api has no Paused value.
     */
    class PauseAndResumeDocumentScenario : Scenario
    {
        public override async Task RunAsync()
        {
            // 1. You need the id of a pending document.
            var result = await CreateDocumentAsync();
            var documentId = result.DocumentId;

            // 2. Pause it. The reason is optional; Signer shows it on the document as statusUpdateReason.
            await CallSignerApiAsync(HttpMethod.Post, $"/api/documents/{documentId}/pause", new
            {
                reason = "Waiting for the contract review"
            });

            var document = await CallSignerApiAsync(HttpMethod.Get, $"/api/documents/{documentId}");
            System.Console.WriteLine($"Document {documentId} is {document["status"]}: {document["statusUpdateReason"]}");

            // 3. Resume it. The request has no body.
            await CallSignerApiAsync(HttpMethod.Post, $"/api/documents/{documentId}/resume");

            document = await CallSignerApiAsync(HttpMethod.Get, $"/api/documents/{documentId}");
            System.Console.WriteLine($"Document {documentId} is {document["status"]}");
        }
    }
}
