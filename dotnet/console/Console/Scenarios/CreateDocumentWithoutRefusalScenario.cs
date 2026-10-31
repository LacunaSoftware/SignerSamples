using System.IO;
using System.Net.Http;
using System.Threading.Tasks;

namespace Console.Scenarios
{
    /**
     * This scenario creates a document that one of its two signers cannot refuse. Signer hides the refuse
     * option from that participant, on the signing and approval pages, and rejects a refusal from them.
     * The setting is per participant: the other signer can still refuse, and your application can still
     * refuse the document on the organization's behalf (RefuseDocumentScenario).
     *
     * FlowActionCreateModel in Lacuna.Signer.Api has no DisableRefusal, so the scenario creates the
     * document with a direct call to the REST API (CallSignerApiAsync, in Scenario.cs).
     */
    class CreateDocumentWithoutRefusalScenario : Scenario
    {
        public override async Task RunAsync()
        {
            // 1. Upload the file with the client library, as in the other scenarios.
            var filePath = "sample.pdf";
            var fileName = Path.GetFileName(filePath);
            var file = File.ReadAllBytes(filePath);
            var uploadModel = await SignerClient.UploadFileAsync(fileName, file, "application/pdf");

            // 2. Create the document. Set disableRefusal on each flow action whose participant must not refuse;
            //    it defaults to false.
            var results = await CallSignerApiAsync(HttpMethod.Post, "/api/documents", new
            {
                files = new[]
                {
                    new { id = uploadModel.Id, name = uploadModel.Name, contentType = uploadModel.ContentType, displayName = "Without Refusal Sample" }
                },
                flowActions = new object[]
                {
                    new
                    {
                        type = "Signer",
                        user = new { name = "Jack Bauer", email = "jack.bauer@mailinator.com", identifier = "75502846369" },
                        disableRefusal = true
                    },
                    new
                    {
                        type = "Signer",
                        user = new { name = "James Bond", email = "james.bond@mailinator.com", identifier = "95588148061" }
                    }
                }
            });
            // One result per file.
            var documentId = (string)results[0]["documentId"];

            // 3. Read the document back: each flow action has disableRefusal.
            var document = await CallSignerApiAsync(HttpMethod.Get, $"/api/documents/{documentId}");
            foreach (var flowAction in document["flowActions"])
            {
                System.Console.WriteLine($"{flowAction["user"]["name"]} can refuse: {!(bool)RequiredField(flowAction, "disableRefusal")}");
            }
        }
    }
}
