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
using Microsoft.AspNetCore.StaticFiles;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json;
using Newtonsoft.Json.Converters;
using Newtonsoft.Json.Linq;
using Newtonsoft.Json.Serialization;

namespace Console.Tests.TestSupport;

/// <summary>
/// A Signer instance on localhost, for the scenarios to call through SIGNER_ENDPOINT. It records
/// every request and answers each route the scenarios use, with a Lacuna.Signer.Api model where the
/// client reads one, serialized as the real API does (camelCase, enums as strings). Any other route,
/// or a document it does not know, answers 404, so a scenario that calls something new or sends the
/// wrong id fails its test instead of passing unchecked.
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
    private readonly ConcurrentQueue<Guid> documentIds = new();
    private readonly ConcurrentQueue<Exception> errors = new();
    private int uploadCount;

    private FakeSigner(WebApplication app)
    {
        this.app = app;
    }

    public string Url { get; private set; } = "";

    public IReadOnlyList<RecordedRequest> Requests => requests.ToArray();

    /// <summary>The documents the fake knows, in the order they were created or added; each create gets a new id.</summary>
    public IReadOnlyList<Guid> DocumentIds => documentIds.ToArray();

    /// <summary>The first document created; fails the test if there is none.</summary>
    public Guid DocumentId => Assert.Single(DocumentIds.Take(1));

    /// <summary>The flow actions that GET /api/documents/{id} returns for every document: one unless a test adds more.</summary>
    public List<Guid> FlowActionIds { get; } = [Guid.NewGuid()];

    public Guid FlowActionId => FlowActionIds[0];

    /// <summary>The exceptions thrown by the fake's own routes, each of which answered 500.</summary>
    public IReadOnlyList<Exception> Errors => errors.ToArray();

    public DocumentStatus DocumentStatus { get; set; } = DocumentStatus.Pending;

    /// <summary>
    /// Fields that GET /api/documents/{id} adds to the DocumentModel, or replaces in it (an array is
    /// replaced whole): the API's fields that Lacuna.Signer.Api 2.1.0 lacks, such as
    /// signerPositioningEnabled, and the status Paused. Pause, resume and PUT /signer-positioning change them.
    /// </summary>
    public JObject DocumentFields { get; } = [];

    public bool HasSignature { get; set; }

    /// <summary>The folders that GET /api/folders searches: a folder matches if its name contains the search text.</summary>
    public List<FolderInfoModel> Folders { get; } = [];

    /// <summary>The bytes of every download, whatever the type.</summary>
    public byte[] DocumentContent { get; } = "%PDF-1.4 fake"u8.ToArray();

    /// <summary>What POST /api/documents/{id}/action-url returns as the participant's signing page.</summary>
    public string ActionUrl => $"{Url}/document/key/the-document-key/sign?ticket=the%2Bticket";

    /// <summary>The hash that the public signature start step hands out to be signed.</summary>
    public byte[] ToSignHash { get; } = System.Security.Cryptography.SHA256.HashData("to sign"u8);

    /// <summary>The digest algorithm that the start step reports: by OID, as the sandbox does, unless a test sets a name.</summary>
    public string DigestAlgorithm { get; set; } = "2.16.840.1.101.3.4.2.1";

    /// <summary>When set, the public signature start step answers Success = false with this error.</summary>
    public string? StartSignatureError { get; set; }

    /// <summary>When set, the public signature complete step answers Success = false with this error.</summary>
    public string? CompleteSignatureError { get; set; }

    /// <summary>When set, GET /api/documents/{id} waits for this task before it answers.</summary>
    public Task? DocumentDetailsDelay { get; set; }

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

    /// <summary>Makes the fake know a document it did not create, such as one a webhook event names.</summary>
    public void AddDocument(Guid id) => documentIds.Enqueue(id);

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
            try
            {
                await next();
            }
            catch (Exception ex)
            {
                // Kestrel answers 500, and logging is off: keep the exception for the test to report.
                errors.Enqueue(ex);
                throw;
            }
        });

        // The client uploads multipart/form-data; the file name is in the part's Content-Disposition.
        // The part has no type, and Signer answers with the one it detects (application/pdf for a PDF).
        app.MapPost("/api/uploads", (HttpContext context) =>
        {
            var name = Regex.Match(Recorded(context).Body, "filename=([^;\r\n]+)").Groups[1].Value;
            return Json(new Lacuna.Signer.Api.UploadModel
            {
                Id = $"upload-{Interlocked.Increment(ref uploadCount)}",
                Name = name,
                ContentType = new FileExtensionContentTypeProvider().TryGetContentType(name, out var contentType) ? contentType : "application/octet-stream",
            });
        });

        app.MapPost("/api/documents", (HttpContext context) =>
        {
            var request = Recorded(context).Json;
            var uploadIds = request["files"]!.Select(file => (string)file["id"]!).ToList();
            // One document per file, unless the files are merged into an envelope.
            if ((bool?)request["isEnvelope"] == true)
            {
                uploadIds = [uploadIds.First()];
            }
            return Json(uploadIds.Select(uploadId =>
            {
                var documentId = Guid.NewGuid();
                documentIds.Enqueue(documentId);
                return new CreateDocumentResult { UploadId = uploadId, DocumentId = documentId };
            }).ToList());
        });
        app.MapGet("/api/documents", () => Json(new PaginatedSearchResponse<DocumentListModel>
        {
            Items = [new DocumentListModel { Id = Guid.NewGuid(), Name = "sample.pdf" }],
            TotalCount = 1,
        }));
        app.MapGet("/api/documents/{id:guid}", async (Guid id) =>
        {
            if (DocumentDetailsDelay != null)
            {
                await DocumentDetailsDelay;
            }
            return IfKnown(id, () => DocumentJson(new DocumentModel
            {
                Id = id,
                Name = "sample.pdf",
                Status = DocumentStatus,
                IsConcluded = DocumentStatus == DocumentStatus.Concluded,
                HasSignature = HasSignature,
                FlowActions = FlowActionIds.Select(flowActionId => new FlowActionModel
                {
                    Id = flowActionId,
                    Type = FlowActionType.Signer,
                    Status = HasSignature ? ActionStatus.Completed : ActionStatus.Pending,
                    User = new ParticipantUserModel { Name = "Jack Bauer", Identifier = "75502846369", Email = "jack.bauer@mailinator.com" },
                }).ToList(),
            }));
        });
        app.MapDelete("/api/documents/{id:guid}", (Guid id) => IfKnown(id, () => Results.Ok()));
        app.MapPost("/api/documents/{id:guid}/versions", (Guid id) => IfKnown(id, () => Results.Ok()));
        app.MapPost("/api/documents/{id:guid}/cancellation", (Guid id) => IfKnown(id, () => Results.Ok()));
        app.MapPost("/api/documents/{id:guid}/refusal", (Guid id) => IfKnown(id, () => Results.Ok()));
        app.MapPost("/api/documents/{id:guid}/pause", (Guid id, HttpContext context) => IfKnown(id, () =>
        {
            DocumentFields["status"] = "Paused";
            DocumentFields["statusUpdateReason"] = Recorded(context).Json["reason"];
            return Results.Ok();
        }));
        app.MapPost("/api/documents/{id:guid}/resume", (Guid id) => IfKnown(id, () =>
        {
            DocumentFields.Remove("status");
            DocumentFields.Remove("statusUpdateReason");
            return Results.Ok();
        }));
        app.MapPut("/api/documents/{id:guid}/signer-positioning", (Guid id, HttpContext context) => IfKnown(id, () =>
        {
            DocumentFields.Merge(Recorded(context).Json);
            return Results.Ok();
        }));
        app.MapPost("/api/documents/{id:guid}/action-url", (Guid id) => IfKnown(id, () => Json(new ActionUrlResponse
        {
            Url = ActionUrl,
            EmbedUrl = $"{Url}/document/embed/the-embed-ticket",
        })));
        app.MapGet("/api/documents/{id:guid}/ticket", (Guid id, string type) => IfKnown(id, () => Json(new TicketModel
        {
            Location = $"{Url}/download/{id}/{type}",
        })));
        // GetDocumentBytesAsync gets a ticket, then downloads its location, without the API key.
        app.MapGet("/download/{id:guid}/{type}", (Guid id) => IfKnown(id, () => Results.Bytes(DocumentContent, "application/pdf")));
        app.MapGet("/api/documents/{id:guid}/content", (Guid id) => IfKnown(id, () => Results.Bytes(DocumentContent, "application/pdf")));
        app.MapGet("/api/documents/{id:guid}/content-b64", (Guid id) => IfKnown(id, () => Json(new { bytes = DocumentContent, name = "sample.pdf", contentType = "application/pdf" })));
        app.MapPost("/api/notifications/flow-action-reminder", () => Results.Ok());
        app.MapPut("/api/invoices/{id:int}/payment", () => Results.Ok());

        // Like Signer's, the search matches part of the name, so it can find more than the folder asked for.
        app.MapGet("/api/folders", (string? q) =>
        {
            var found = Folders.Where(folder => q == null || folder.Name?.Contains(q, StringComparison.OrdinalIgnoreCase) == true).ToList();
            return Json(new PaginatedSearchResponse<FolderInfoModel> { Items = found, TotalCount = found.Count });
        });

        // Success and ValidationResults say whether Signer accepted the certificate and the signature.
        app.MapPost("/api/documents/keys/{key}/signature/certificate", () => Json(StartSignatureError == null
            ? new StartSignatureResponse
            {
                Success = true,
                ToSignHash = ToSignHash,
                DigestAlgorithm = DigestAlgorithm,
                Token = "the-signature-token",
            }
            : new StartSignatureResponse { Success = false, ValidationResults = ValidationErrors(StartSignatureError) }));
        app.MapPost("/api/documents/keys/{key}/signature", () => Json(CompleteSignatureError == null
            ? new CompleteSignatureResponse { Success = true }
            : new CompleteSignatureResponse { Success = false, ValidationResults = ValidationErrors(CompleteSignatureError) }));

        app.MapFallback(() => Results.NotFound());
    }

    private IResult IfKnown(Guid id, Func<IResult> answer) => DocumentIds.Contains(id) ? answer() : Results.NotFound();

    private static ValidationResultsModel ValidationErrors(string message) => new()
    {
        Errors = [new ValidationItemModel { Type = "TestRejection", Message = message }],
    };

    private static RecordedRequest Recorded(HttpContext context) => (RecordedRequest)context.Items[RequestKey]!;

    private static IResult Json(object value) =>
        Results.Content(JsonConvert.SerializeObject(value, JsonSettings), "application/json");

    private IResult DocumentJson(DocumentModel document)
    {
        var json = JObject.FromObject(document, JsonSerializer.Create(JsonSettings));
        json.Merge(DocumentFields, new JsonMergeSettings { MergeArrayHandling = MergeArrayHandling.Replace });
        return Results.Content(json.ToString(), "application/json");
    }

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
