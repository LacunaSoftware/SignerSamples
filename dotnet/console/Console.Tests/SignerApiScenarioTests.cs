using System.Text.RegularExpressions;
using Console.Scenarios;
using Console.Tests.TestSupport;
using Newtonsoft.Json.Linq;

namespace Console.Tests;

/// <summary>
/// The scenarios for features newer than Lacuna.Signer.Client: they upload with the client, then call
/// the API through the base class's CallSignerApiAsync, with the API reference's camelCase names,
/// and read back fields that Lacuna.Signer.Api lacks (set here in the fake's DocumentFields).
/// </summary>
public class SignerApiScenarioTests : FakeSignerTest
{
    private string DocumentPath => $"/api/documents/{Signer.DocumentId}";

    [Fact]
    public async Task Pause_sends_a_reason_and_resume_sends_no_body()
    {
        var output = await RunAsync<PauseAndResumeDocumentScenario>();

        Assert.Equal("Waiting for the contract review", (string?)Signer.Single("POST", $"{DocumentPath}/pause").Json["reason"]);
        Assert.Empty(Signer.Single("POST", $"{DocumentPath}/resume").Body);
        Assert.All(Signer.Requests, request => Assert.Equal(ApiKey, request.ApiKey));
        Assert.Matches(new Regex($"is Paused: Waiting for the contract review\\s+Document {Signer.DocumentId} is Pending"), output);
    }

    [Fact]
    public async Task Without_refusal_disables_refusal_for_the_first_signer_only()
    {
        Signer.DocumentFields["flowActions"] = new JArray(
            new JObject { ["user"] = new JObject { ["name"] = "Jack Bauer" }, ["disableRefusal"] = true },
            new JObject { ["user"] = new JObject { ["name"] = "James Bond" }, ["disableRefusal"] = false });

        var output = await RunAsync<CreateDocumentWithoutRefusalScenario>();

        var request = CreateRequest();
        AssertFile(request, "Without Refusal Sample");
        var actions = request["flowActions"]!.ToList();
        Assert.Equal(["75502846369", "95588148061"], actions.Select(action => (string?)action["user"]!["identifier"]));
        Assert.True((bool)actions[0]["disableRefusal"]!);
        Assert.Null(actions[1]["disableRefusal"]);
        Assert.Contains("Jack Bauer can refuse: False", output);
        Assert.Contains("James Bond can refuse: True", output);
    }

    [Fact]
    public async Task Signer_positioning_is_set_on_create_then_changed_before_the_first_signature()
    {
        Signer.DocumentFields["signerPositioningEnabled"] = true;
        Signer.DocumentFields["automaticSignaturePositioning"] = "SignatureSheet";

        var output = await RunAsync<CreateDocumentWithSignerPositioningScenario>();

        var request = CreateRequest();
        AssertFile(request, "Signer Positioning Sample");
        Assert.True((bool)request["signerPositioningEnabled"]!);
        Assert.Equal("SignatureSheet", (string?)request["automaticSignaturePositioning"]);
        var change = Signer.Single("PUT", $"{DocumentPath}/signer-positioning").Json;
        Assert.False((bool)change["signerPositioningEnabled"]!);
        Assert.Equal("Footnote", (string?)change["automaticSignaturePositioning"]);
        Assert.Matches(new Regex("signerPositioningEnabled = True, automaticSignaturePositioning = SignatureSheet\\s+.*signerPositioningEnabled = False, automaticSignaturePositioning = Footnote"), output);
    }

    [Fact]
    public async Task Notification_language_is_set_per_signer_and_per_notified_address()
    {
        Signer.DocumentFields["flowActions"] = new JArray(
            new JObject { ["user"] = new JObject { ["email"] = "jack.bauer@mailinator.com" }, ["notificationLanguage"] = "En" },
            new JObject { ["user"] = new JObject { ["email"] = "james.bond@mailinator.com" }, ["notificationLanguage"] = "Es" });
        Signer.DocumentFields["notifiedEmailsData"] = new JArray(
            new JObject { ["email"] = "jill.palmer@mailinator.com", ["notificationLanguage"] = "Pt" });

        var output = await RunAsync<CreateDocumentWithNotificationLanguageScenario>();

        var request = CreateRequest();
        AssertFile(request, "Notification Language Sample");
        Assert.Equal(["En", "Es"], request["flowActions"]!.Select(action => (string?)action["notificationLanguage"]));
        var notified = Assert.Single(request["notifiedEmailsData"]!);
        Assert.Equal("jill.palmer@mailinator.com", (string?)notified["email"]);
        Assert.Equal("Pt", (string?)notified["notificationLanguage"]);
        Assert.Contains("jack.bauer@mailinator.com is notified in En", output);
        Assert.Contains("james.bond@mailinator.com is notified in Es", output);
        Assert.Contains("jill.palmer@mailinator.com learns of the conclusion in Pt", output);
    }

    [Fact]
    public async Task Pdfa_conversion_uses_the_pdfa2b_profile_with_a_timestamp()
    {
        Signer.DocumentFields["timestampEnabled"] = true;

        var output = await RunAsync<CreatePdfADocumentWithTimestampScenario>();

        var request = CreateRequest();
        AssertFile(request, "PDF/A With Timestamp Sample");
        Assert.True((bool)request["convertToPdfA"]!);
        Assert.Equal("PdfA2b", (string?)request["pdfAProfile"]);
        Assert.True((bool)request["timestampEnabled"]!);
        Assert.Contains($"Document {Signer.DocumentId}: timestampEnabled = True", output);
    }

    private JToken CreateRequest() => Signer.Single("POST", "/api/documents").Json;

    // The file uploaded with the client, sent with the name and type the upload returned.
    private static void AssertFile(JToken request, string displayName)
    {
        var file = Assert.Single(request["files"]!);
        Assert.Equal("upload-1", (string?)file["id"]);
        Assert.Equal("sample.pdf", (string?)file["name"]);
        Assert.Equal("application/pdf", (string?)file["contentType"]);
        Assert.Equal(displayName, (string?)file["displayName"]);
    }
}
