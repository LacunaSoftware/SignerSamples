using System.IO;
using System.Net.Http;
using System.Threading.Tasks;

namespace Console.Scenarios
{
    /**
     * This scenario chooses the language of each e-mail that Signer sends about a document:
     *
     * - notificationLanguage on a flow action: the e-mails to that participant.
     * - notifiedEmailsData on the document: the addresses told when the document is concluded, each with
     *   its own language. It replaces the older notifiedEmails list of addresses.
     *
     * The languages are Pt, Es and En; without one, Signer uses the instance's default language.
     *
     * Lacuna.Signer.Api has neither field, so the scenario creates the document with a direct call
     * to the REST API (CallSignerApiAsync, in Scenario.cs).
     */
    class CreateDocumentWithNotificationLanguageScenario : Scenario
    {
        public override async Task RunAsync()
        {
            // 1. Upload the file with the client library, as in the other scenarios.
            var filePath = "sample.pdf";
            var fileName = Path.GetFileName(filePath);
            var file = File.ReadAllBytes(filePath);
            var uploadModel = await SignerClient.UploadFileAsync(fileName, file, "application/pdf");

            // 2. Create the document: one signer is notified in English, the other in Spanish, and a third
            //    address learns in Portuguese that the document is concluded.
            var results = await CallSignerApiAsync(HttpMethod.Post, "/api/documents", new
            {
                files = new[]
                {
                    new { id = uploadModel.Id, name = uploadModel.Name, contentType = uploadModel.ContentType, displayName = "Notification Language Sample" }
                },
                flowActions = new[]
                {
                    new
                    {
                        type = "Signer",
                        user = new { name = "Jack Bauer", email = "jack.bauer@mailinator.com", identifier = "75502846369" },
                        notificationLanguage = "En"
                    },
                    new
                    {
                        type = "Signer",
                        user = new { name = "James Bond", email = "james.bond@mailinator.com", identifier = "95588148061" },
                        notificationLanguage = "Es"
                    }
                },
                notifiedEmailsData = new[]
                {
                    new { email = "jill.palmer@mailinator.com", notificationLanguage = "Pt" }
                }
            });
            var documentId = (string)results[0]["documentId"];

            // 3. Read the document back.
            var document = await CallSignerApiAsync(HttpMethod.Get, $"/api/documents/{documentId}");
            foreach (var flowAction in document["flowActions"])
            {
                System.Console.WriteLine($"{flowAction["user"]["email"]} is notified in {RequiredField(flowAction, "notificationLanguage")}");
            }
            foreach (var notifiedEmail in RequiredField(document, "notifiedEmailsData"))
            {
                System.Console.WriteLine($"{notifiedEmail["email"]} learns of the conclusion in {RequiredField(notifiedEmail, "notificationLanguage")}");
            }
        }
    }
}
