using System.Net;
using System.Net.Http.Headers;
using System.Net.Sockets;
using System.Text;
using Console.Scenarios;
using Console.Tests.TestSupport;
using Lacuna.Signer.Api;

namespace Console.Tests;

/// <summary>
/// Starts WebhookEndpointScenario once, on a free port, with its own fake Signer. The endpoint
/// reads SIGNER_WEBHOOK_PORT and SIGNER_WEBHOOK_TOKEN into static fields, so one endpoint serves
/// the whole test run; it never stops, and dies with the test process.
/// </summary>
public sealed class WebhookEndpointFixture : IAsyncLifetime
{
    public const string Token = "webhook-tests-token";

    public FakeSigner Signer { get; private set; } = null!;

    public HttpClient Http { get; private set; } = null!;

    public async ValueTask InitializeAsync()
    {
        Signer = await FakeSigner.StartAsync();
        var port = FreePort();
        Task listening;
        using (new EnvironmentVariables(new Dictionary<string, string?>
        {
            ["SIGNER_ENDPOINT"] = Signer.Url,
            ["SIGNER_API_KEY"] = "Console Tests|0123456789abcdef",
            ["SIGNER_WEBHOOK_PORT"] = port.ToString(),
            ["SIGNER_WEBHOOK_TOKEN"] = Token,
        }))
        {
            // RunAsync reads the port and token, and starts listening, before its first await.
            using var console = new ConsoleCapture();
            listening = new WebhookEndpointScenario().RunAsync();
        }
        if (listening.IsFaulted)
        {
            await listening;
        }
        Http = new HttpClient { BaseAddress = new Uri($"http://localhost:{port}/") };
    }

    public async ValueTask DisposeAsync()
    {
        Http.Dispose();
        await Signer.DisposeAsync();
    }

    private static int FreePort()
    {
        using var listener = new TcpListener(IPAddress.Loopback, 0);
        listener.Start();
        return ((IPEndPoint)listener.LocalEndpoint).Port;
    }
}

/// <summary>
/// The webhook endpoint's rules: check the token, answer at once, ignore repeated events, and read
/// the document back before acting on DocumentConcluded.
/// </summary>
public class WebhookEndpointTests(WebhookEndpointFixture endpoint) : IClassFixture<WebhookEndpointFixture>
{
    [Fact]
    public async Task Event_without_the_token_answers_401()
    {
        var response = await PostAsync(SignedEvent(Guid.NewGuid(), Guid.NewGuid()), token: null);

        Assert.Equal(HttpStatusCode.Unauthorized, response.StatusCode);
    }

    [Fact]
    public async Task Event_with_another_token_answers_401()
    {
        var response = await PostAsync(SignedEvent(Guid.NewGuid(), Guid.NewGuid()), token: "not-the-token");

        Assert.Equal(HttpStatusCode.Unauthorized, response.StatusCode);
    }

    [Fact]
    public async Task Get_and_other_paths_answer_404()
    {
        Assert.Equal(HttpStatusCode.NotFound, (await endpoint.Http.GetAsync("webhook", TestContext.Current.CancellationToken)).StatusCode);
        Assert.Equal(HttpStatusCode.NotFound, (await PostAsync(SignedEvent(Guid.NewGuid(), Guid.NewGuid()), path: "other")).StatusCode);
    }

    [Theory]
    [InlineData("not json")]
    [InlineData("""{ "data": { "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6" } }""")]
    public async Task Body_that_is_not_a_webhook_answers_400(string body)
    {
        Assert.Equal(HttpStatusCode.BadRequest, (await PostAsync(body)).StatusCode);
    }

    [Fact]
    public async Task Signed_event_is_accepted_then_handled()
    {
        var documentId = Guid.NewGuid();
        var flowActionId = Guid.NewGuid();
        using var console = new ConsoleCapture();

        var response = await PostAsync(SignedEvent(documentId, flowActionId));

        Assert.Equal(HttpStatusCode.OK, response.StatusCode);
        await console.WaitForAsync($"Accepted: DocumentSigned:{documentId}:{flowActionId}");
        await console.WaitForAsync($"Document {documentId} is signed!");
    }

    [Fact]
    public async Task Repeated_event_answers_200_but_is_handled_once()
    {
        var documentId = Guid.NewGuid();
        var flowActionId = Guid.NewGuid();
        using var console = new ConsoleCapture();

        await PostAsync(SignedEvent(documentId, flowActionId));
        await console.WaitForAsync($"Document {documentId} is signed!");
        var repeated = await PostAsync(SignedEvent(documentId, flowActionId));

        Assert.Equal(HttpStatusCode.OK, repeated.StatusCode);
        var output = await console.WaitForAsync($"Ignored a repeated event: DocumentSigned:{documentId}:{flowActionId}");
        Assert.Single(output.Split('\n'), line => line.Contains($"Document {documentId} is signed!"));
    }

    [Fact]
    public async Task Signatures_of_two_participants_are_two_events()
    {
        var documentId = Guid.NewGuid();
        var first = Guid.NewGuid();
        var second = Guid.NewGuid();
        using var console = new ConsoleCapture();

        await PostAsync(SignedEvent(documentId, first));
        await PostAsync(SignedEvent(documentId, second));

        await console.WaitForAsync($"Accepted: DocumentSigned:{documentId}:{first}");
        await console.WaitForAsync($"Accepted: DocumentSigned:{documentId}:{second}");
    }

    [Fact]
    public async Task Concluded_event_is_handled_after_reading_the_document_as_concluded()
    {
        var documentId = Guid.NewGuid();
        endpoint.Signer.DocumentStatus = DocumentStatus.Concluded;
        using var console = new ConsoleCapture();

        Assert.Equal(HttpStatusCode.OK, (await PostAsync(ConcludedEvent(documentId))).StatusCode);

        await console.WaitForAsync($"Document {documentId} is concluded!");
        endpoint.Signer.Single("GET", $"/api/documents/{documentId}");
    }

    [Fact]
    public async Task Concluded_event_for_a_document_still_pending_does_nothing()
    {
        var documentId = Guid.NewGuid();
        endpoint.Signer.DocumentStatus = DocumentStatus.Pending;
        using var console = new ConsoleCapture();

        Assert.Equal(HttpStatusCode.OK, (await PostAsync(ConcludedEvent(documentId))).StatusCode);

        var output = await console.WaitForAsync($"Document {documentId} is Pending, not Concluded: nothing to do yet");
        Assert.DoesNotContain($"Document {documentId} is concluded!", output);
    }

    [Fact]
    public async Task Deleted_event_is_accepted_though_the_sdk_does_not_know_its_type()
    {
        var first = Guid.NewGuid();
        var second = Guid.NewGuid();
        using var console = new ConsoleCapture();

        var response = await PostAsync($$"""{ "type": "DocumentDeleted", "data": { "documents": [ { "id": "{{first}}" }, { "id": "{{second}}" } ] } }""");

        Assert.Equal(HttpStatusCode.OK, response.StatusCode);
        await console.WaitForAsync($"Accepted: DocumentDeleted:{first},{second}");
        await console.WaitForAsync("This sample does not handle DocumentDeleted events");
    }

    private static string SignedEvent(Guid documentId, Guid flowActionId) => $$"""
        {
          "type": "DocumentSigned",
          "data": {
            "id": "{{documentId}}",
            "name": "sample.pdf",
            "signature": { "flowActionId": "{{flowActionId}}", "name": "Jack Bauer", "identifier": "75502846369", "emailAddress": "jack.bauer@mailinator.com" }
          }
        }
        """;

    private static string ConcludedEvent(Guid documentId) => $$"""{ "type": "DocumentConcluded", "data": { "id": "{{documentId}}", "name": "sample.pdf" } }""";

    private async Task<HttpResponseMessage> PostAsync(string body, string? token = WebhookEndpointFixture.Token, string path = "webhook")
    {
        using var request = new HttpRequestMessage(HttpMethod.Post, path)
        {
            Content = new StringContent(body, Encoding.UTF8, "application/json"),
        };
        if (token != null)
        {
            request.Headers.Authorization = new AuthenticationHeaderValue("Bearer", token);
        }
        return await endpoint.Http.SendAsync(request, TestContext.Current.CancellationToken);
    }
}
