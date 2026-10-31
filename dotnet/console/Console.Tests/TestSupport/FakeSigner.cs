using System.Collections.Concurrent;
using System.Text.RegularExpressions;
using Lacuna.Signer.Api;
using Lacuna.Signer.Api.Documents;
using Lacuna.Signer.Api.FlowActions;
using Lacuna.Signer.Api.Folders;
using Lacuna.Signer.Api.Signature;
using Lacuna.Signer.Api.Users;
using Lacuna.Spa.Api;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json;
using Newtonsoft.Json.Converters;
using Newtonsoft.Json.Linq;
using Newtonsoft.Json.Serialization;

namespace Console.Tests.TestSupport;

/// <summary>
/// A Signer instance on localhost, for the scenarios to call through SIGNER_ENDPOINT. It records
/// every request and answers each route the scenarios use with a model from Lacuna.Signer.Api,
/// serialized as the real API does (camelCase, enums as strings). Any other route answers 404, so
/// a scenario that calls something new fails its test instead of passing unchecked.
/// </summary>
public sealed class FakeSigner : IAsyncDisposable
{
    public static readonly JsonSerializerSettings JsonSettings = new()
    {
        ContractResolver = new CamelCasePropertyNamesContractResolver(),
        Converters = { new StringEnumConverter() },
        NullValueHandling = NullValueHandling.Ignore,
    };

    private const string RequestKey = "request";

    private readonly WebApplication app;
    private readonly ConcurrentQueue<RecordedRequest> requests = new();
    private int uploadCount;

    private FakeSigner(WebApplication app)
    {
        this.app = app;
    }

    public string Url { get; private set; } = "";

    public IReadOnlyList<RecordedRequest> Requests => requests.ToArray();

    /// <summary>The id of every document created; GET /api/documents/{id} answers for any id.</summary>
    public Guid DocumentId { get; } = Guid.NewGuid();

    /// <summary>The flow action of the document, which GET /api/documents/{id} returns.</summary>
    public Guid FlowActionId { get; } = Guid.NewGuid();

    public DocumentStatus DocumentStatus { get; set; } = DocumentStatus.Pending;

    public bool HasSignature { get; set; }

    /// <summary>The folders that GET /api/folders finds, whatever the search.</summary>
    public List<FolderInfoModel> Folders { get; } = [];

    /// <summary>The bytes of every download, whatever the type.</summary>
    public byte[] DocumentContent { get; } = "%PDF-1.4 fake"u8.ToArray();

    /// <summary>What POST /api/documents/{id}/action-url returns as the participant's signing page.</summary>
    public string ActionUrl => $"{Url}/document/key/the-document-key/sign?ticket=the%2Bticket";

    /// <summary>The hash that the public signature start step hands out to be signed.</summary>
    public byte[] ToSignHash { get; } = System.Security.Cryptography.SHA256.HashData("to sign"u8);

    /// <summary>The digest algorithm that the start step reports: by OID, as the sandbox does, unless a test sets a name.</summary>
    public string DigestAlgorithm { get; set; } = "2.16.840.1.101.3.4.2.1";

    public static async Task<FakeSigner> StartAsync()
    {
        var builder = WebApplication.CreateSlimBuilder();
        builder.Logging.ClearProviders();
        builder.WebHost.UseUrls("http://127.0.0.1:0");
        var fake = new FakeSigner(builder.Build());
        fake.MapRoutes();
        await fake.app.StartAsync();
        fake.Url = fake.app.Urls.Single();
        return fake;
    }

    /// <summary>The requests to a path, such as "/api/documents", in the order they arrived.</summary>
    public IReadOnlyList<RecordedRequest> RequestsTo(string method, string path) =>
        Requests.Where(r => r.Method == method && r.Path == path).ToList();

    /// <summary>The single request to a path; fails the test unless there is exactly one.</summary>
    public RecordedRequest Single(string method, string path) => Assert.Single(RequestsTo(method, path));

    private void MapRoutes()
    {
        app.Use(async (context, next) =>
        {
            var body = await new StreamReader(context.Request.Body).ReadToEndAsync();
            var request = new RecordedRequest(
                context.Request.Method,
                context.Request.Path.Value ?? "",
                context.Request.Query.ToDictionary(q => q.Key, q => q.Value.ToString(), StringComparer.OrdinalIgnoreCase),
                context.Request.Headers["X-Api-Key"].ToString(),
                body);
            context.Items[RequestKey] = request;
            requests.Enqueue(request);
            await next();
        });

        // The client uploads multipart/form-data; the file name is in the part's Content-Disposition.
        app.MapPost("/api/uploads", (HttpContext context) => Json(new Lacuna.Signer.Api.UploadModel
        {
            Id = $"upload-{Interlocked.Increment(ref uploadCount)}",
            Name = Regex.Match(Recorded(context).Body, "filename=([^;\r\n]+)").Groups[1].Value,
        }));

        app.MapPost("/api/documents", (HttpContext context) =>
        {
            var request = Recorded(context).Json;
            var uploadIds = request["files"]!.Select(file => (string)file["id"]!).ToList();
            // One document per file, unless the files are merged into an envelope.
            if ((bool?)request["isEnvelope"] == true)
            {
                uploadIds = [uploadIds.First()];
            }
            return Json(uploadIds.Select(uploadId => new CreateDocumentResult { UploadId = uploadId, DocumentId = DocumentId }).ToList());
        });
        app.MapGet("/api/documents", () => Json(new PaginatedSearchResponse<DocumentListModel>
        {
            Items = [new DocumentListModel { Id = DocumentId, Name = "sample.pdf" }],
            TotalCount = 1,
        }));
        app.MapGet("/api/documents/{id:guid}", (Guid id) => Json(new DocumentModel
        {
            Id = id,
            Name = "sample.pdf",
            Status = DocumentStatus,
            IsConcluded = DocumentStatus == DocumentStatus.Concluded,
            HasSignature = HasSignature,
            FlowActions =
            [
                new FlowActionModel
                {
                    Id = FlowActionId,
                    Type = FlowActionType.Signer,
                    Status = HasSignature ? ActionStatus.Completed : ActionStatus.Pending,
                    User = new ParticipantUserModel { Name = "Jack Bauer", Identifier = "75502846369", Email = "jack.bauer@mailinator.com" },
                },
            ],
        }));
        app.MapDelete("/api/documents/{id:guid}", () => Results.Ok());
        app.MapPost("/api/documents/{id:guid}/versions", () => Results.Ok());
        app.MapPost("/api/documents/{id:guid}/cancellation", () => Results.Ok());
        app.MapPost("/api/documents/{id:guid}/refusal", () => Results.Ok());
        app.MapPost("/api/documents/{id:guid}/action-url", () => Json(new ActionUrlResponse
        {
            Url = ActionUrl,
            EmbedUrl = $"{Url}/document/embed/the-embed-ticket",
        }));
        app.MapGet("/api/documents/{id:guid}/ticket", (Guid id, string type) => Json(new TicketModel
        {
            Location = $"{Url}/download/{id}/{type}",
        }));
        // GetDocumentBytesAsync gets a ticket, then downloads its location, without the API key.
        app.MapGet("/download/{id:guid}/{type}", () => Results.Bytes(DocumentContent, "application/pdf"));
        app.MapGet("/api/documents/{id:guid}/content", () => Results.Bytes(DocumentContent, "application/pdf"));
        app.MapGet("/api/documents/{id:guid}/content-b64", () => Json(new { bytes = DocumentContent, name = "sample.pdf", contentType = "application/pdf" }));
        app.MapPost("/api/notifications/flow-action-reminder", () => Results.Ok());
        app.MapPut("/api/invoices/{id:int}/payment", () => Results.Ok());

        app.MapGet("/api/folders", () => Json(new PaginatedSearchResponse<FolderInfoModel> { Items = Folders, TotalCount = Folders.Count }));

        app.MapPost("/api/documents/keys/{key}/signature/certificate", () => Json(new StartSignatureResponse
        {
            Success = true,
            ToSignHash = ToSignHash,
            DigestAlgorithm = DigestAlgorithm,
            Token = "the-signature-token",
        }));
        app.MapPost("/api/documents/keys/{key}/signature", () => Json(new CompleteSignatureResponse()));

        app.MapFallback(() => Results.NotFound());
    }

    private static RecordedRequest Recorded(HttpContext context) => (RecordedRequest)context.Items[RequestKey]!;

    private static IResult Json(object value) =>
        Results.Content(JsonConvert.SerializeObject(value, JsonSettings), "application/json");

    public ValueTask DisposeAsync() => app.DisposeAsync();
}

/// <summary>A request as the fake Signer received it.</summary>
public record RecordedRequest(string Method, string Path, IReadOnlyDictionary<string, string> Query, string ApiKey, string Body)
{
    /// <summary>
    /// The JSON body with camelCase names, as the API reference spells them ("flowActions"). The
    /// client sends PascalCase ("FlowActions"), which the API accepts too.
    /// </summary>
    public JToken Json => ToCamelCase(JToken.Parse(Body));

    private static JToken ToCamelCase(JToken token) => token switch
    {
        JObject obj => new JObject(obj.Properties().Select(p => new JProperty(char.ToLowerInvariant(p.Name[0]) + p.Name[1..], ToCamelCase(p.Value)))),
        JArray array => new JArray(array.Select(ToCamelCase)),
        _ => token,
    };
}
