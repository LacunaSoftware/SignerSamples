using Console.Scenarios;
using Console.Tests.TestSupport;
using Lacuna.Signer.Api;

namespace Console.Tests;

/// <summary>
/// The scenarios that act on a document after creating it with the base class's CreateDocumentAsync,
/// and the ones that list documents or update an invoice.
/// </summary>
public class DocumentScenarioTests : FakeSignerTest
{
    private static readonly string[] DownloadedFiles = ["downloaded-original.pdf", "downloaded-signed.pdf", "downloaded-printer-friendly.pdf"];

    private string DocumentPath => $"/api/documents/{Signer.DocumentId}";

    [Fact]
    public async Task Every_call_sends_the_api_key_from_SIGNER_API_KEY()
    {
        await RunAsync<CheckDocumentStatusScenario>();

        Assert.NotEmpty(Signer.Requests);
        Assert.All(Signer.Requests, request => Assert.Equal(ApiKey, request.ApiKey));
    }

    [Fact]
    public async Task Check_status_creates_a_document_then_reads_it()
    {
        await RunAsync<CheckDocumentStatusScenario>();

        Assert.Equal("Check Status Sample", (string?)Assert.Single(Signer.Single("POST", "/api/documents").Json["files"]!)["displayName"]);
        Signer.Single("GET", DocumentPath);
    }

    [Fact]
    public async Task Reminder_is_sent_for_each_flow_action_of_the_document()
    {
        await RunAsync<NotifyFlowParticipantsScenario>();

        var reminder = Signer.Single("POST", "/api/notifications/flow-action-reminder").Json;
        Assert.Equal(Signer.DocumentId, (Guid)reminder["documentId"]!);
        Assert.Equal(Signer.FlowActionId, (Guid)reminder["flowActionId"]!);
    }

    [Fact]
    public async Task List_filters_pending_documents_by_name_newest_first()
    {
        var output = await RunAsync<ListDocumentsScenario>();

        var query = Signer.Single("GET", "/api/documents").Query;
        Assert.Equal("Pending", query["Status"]);
        Assert.Equal("TestDocumentName", query["Q"]);
        Assert.Equal("10", query["Limit"]);
        Assert.Equal("Desc", query["Order"]);
        Assert.Contains("sample.pdf", output);
    }

    [Fact]
    public async Task Cancel_sends_a_reason()
    {
        await RunAsync<CancelDocumentScenario>();

        Assert.Equal("This is a document cancellation", (string?)Signer.Single("POST", $"{DocumentPath}/cancellation").Json["reason"]);
    }

    [Fact]
    public async Task Refuse_sends_a_reason()
    {
        await RunAsync<RefuseDocumentScenario>();

        Assert.Equal("This is a document refusal", (string?)Signer.Single("POST", $"{DocumentPath}/refusal").Json["reason"]);
    }

    [Fact]
    public async Task Delete_deletes_the_document_it_created()
    {
        await RunAsync<DeleteDocumentScenario>();

        Signer.Single("DELETE", DocumentPath);
    }

    [Fact]
    public async Task New_version_uploads_the_file_again_and_posts_it_as_a_version()
    {
        await RunAsync<AddNewDocumentVersionScenario>();

        Assert.Equal(2, Signer.RequestsTo("POST", "/api/uploads").Count);
        var file = Signer.Single("POST", $"{DocumentPath}/versions").Json["file"]!;
        Assert.Equal("upload-2", (string?)file["id"]);
        Assert.Equal("Add New Document Version Sample", (string?)file["displayName"]);
    }

    [Fact]
    public async Task Download_of_an_unsigned_document_gets_only_the_original()
    {
        DeleteDownloadedFiles();

        var output = await RunAsync<DownloadDocumentVersionScenario>();

        Assert.Equal("Original", Signer.Single("GET", $"{DocumentPath}/content").Query["type"]);
        Assert.All(Signer.RequestsTo("GET", $"{DocumentPath}/ticket"), ticket => Assert.Equal("Original", ticket.Query["type"]));
        Assert.Equal(Signer.DocumentContent, File.ReadAllBytes("downloaded-original.pdf"));
        Assert.False(File.Exists("downloaded-signed.pdf"));
        Assert.Contains("has no signatures yet", output);
    }

    [Fact]
    public async Task Download_of_a_signed_document_gets_the_signed_and_printer_friendly_versions()
    {
        DeleteDownloadedFiles();
        Signer.HasSignature = true;

        await RunAsync<DownloadDocumentVersionScenario>();

        Assert.Equal(["Original", "Signatures", "PrinterFriendlyVersion"], ContentTypesRequested());
        Assert.All(DownloadedFiles, file => Assert.Equal(Signer.DocumentContent, File.ReadAllBytes(file)));
    }

    [Theory]
    [InlineData(DocumentStatus.Canceled)]
    [InlineData(DocumentStatus.Refused)]
    [InlineData(DocumentStatus.Expired)]
    public async Task Download_skips_the_signed_file_of_a_document_that_did_not_conclude(DocumentStatus status)
    {
        DeleteDownloadedFiles();
        Signer.HasSignature = true;
        Signer.DocumentStatus = status;

        var output = await RunAsync<DownloadDocumentVersionScenario>();

        Assert.Equal(["Original", "PrinterFriendlyVersion"], ContentTypesRequested());
        Assert.Contains($"is {status}, so its signed file cannot be downloaded", output);
    }

    [Fact]
    public async Task Invoice_is_marked_as_paid()
    {
        await RunAsync<InvoiceWebhookHandlingScenario>();

        Assert.True((bool)Signer.Single("PUT", "/api/invoices/1/payment").Json["isPaid"]!);
    }

    private List<string> ContentTypesRequested() =>
        Signer.RequestsTo("GET", $"{DocumentPath}/content").Select(request => request.Query["type"]).ToList();

    // The download scenario saves its files in the working folder, the test's output folder.
    private static void DeleteDownloadedFiles()
    {
        foreach (var file in DownloadedFiles)
        {
            File.Delete(Path.Combine(AppContext.BaseDirectory, file));
        }
    }
}
