using System.Text.RegularExpressions;
using Console.Scenarios;
using Console.Tests.TestSupport;
using Lacuna.Signer.Api;
using Lacuna.Signer.Client;

namespace Console.Tests;

/// <summary>
/// Runs the scenarios as they are against a real Signer: SIGNER_ENDPOINT and SIGNER_API_KEY, or
/// the sandbox and its shared key when they are unset, as `dotnet run` does. Skipped unless
/// SIGNER_LIVE_TESTS is 1, because every run creates documents there and Signer e-mails the sample
/// participants (made-up @mailinator.com addresses).
///
/// Left out: CreateDocumentInExistingFolderScenario (needs a folder named "Sample Folder"),
/// InvoiceWebhookHandlingScenario.RunAsync (needs a billing key and a real invoice) and the
/// webhook endpoint (runs until stopped).
/// </summary>
public class LiveSandboxTests : IDisposable
{
    private readonly string previousDirectory = Environment.CurrentDirectory;

    public LiveSandboxTests()
    {
        Environment.CurrentDirectory = AppContext.BaseDirectory;
    }

    public static TheoryData<Type> Scenarios =>
    [
        typeof(CreateDocumentWithOneSignerScenario),
        typeof(CreateDocumentWithDescriptionScenario),
        typeof(CreateDocumentWithAttachmentScenario),
        typeof(CreateDocumentWithEnvelopeScenario),
        typeof(CreateDocumentWithTwoOrMoreSignersWithOrderScenario),
        typeof(CreateDocumentWithTwoOrMoreSignersWithoutOrderScenario),
        typeof(CreateDocumentWithApproversScenario),
        typeof(CreateDocumentWithSigningRuleScenario),
        typeof(CreateDocumentWithPositionedSignatures),
        typeof(CreatePDFDocumentWithCadesSignatureScenario),
        typeof(CreateXMLWithFullDocumentSignatureScenario),
        typeof(CreateXMLWithElementSignatureScenario),
        typeof(CreateDigitalDegreeScenario),
        typeof(CreateDocumentInNewFolderScenario),
        typeof(EmbeddedSignatureScenario),
        typeof(PublicSignatureScenario),
        typeof(CheckDocumentStatusScenario),
        typeof(NotifyFlowParticipantsScenario),
        typeof(ListDocumentsScenario),
        typeof(DownloadDocumentVersionScenario),
        typeof(CancelDocumentScenario),
        typeof(RefuseDocumentScenario),
        typeof(DeleteDocumentScenario),
        typeof(AddNewDocumentVersionScenario),
    ];

    [Theory]
    [MemberData(nameof(Scenarios))]
    public async Task Scenario_runs_against_the_live_signer(Type scenarioType)
    {
        Assert.SkipUnless(Environment.GetEnvironmentVariable("SIGNER_LIVE_TESTS") == "1", "Set SIGNER_LIVE_TESTS=1 to run the scenarios against the Signer sandbox (or SIGNER_ENDPOINT).");

        string output;
        using (var console = new ConsoleCapture())
        {
            await ((Scenario)Activator.CreateInstance(scenarioType, nonPublic: true)!).RunAsync();
            output = console.Text;
        }

        // The scenarios that print "Document <id> created" made a document: read it back.
        foreach (Match created in Regex.Matches(output, @"Document ([0-9a-f-]{36}) created"))
        {
            var document = await new SignerClientAccess().Client.GetDocumentDetailsAsync(Guid.Parse(created.Groups[1].Value));
            Assert.NotEmpty(document.FlowActions);
            if (scenarioType != typeof(PublicSignatureScenario))
            {
                Assert.Equal(DocumentStatus.Pending, document.Status);
            }
        }
    }

    public void Dispose()
    {
        Environment.CurrentDirectory = previousDirectory;
    }

    // The SignerClient that the scenarios build from the environment.
    private class SignerClientAccess : Scenario
    {
        public SignerClient Client => SignerClient;

        public override Task RunAsync() => Task.CompletedTask;
    }
}
