using Console.Scenarios;
using Console.Tests.TestSupport;
using Lacuna.Signer.Api;
using Lacuna.Signer.Api.Invoices;
using Lacuna.Signer.Api.Webhooks;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

namespace Console.Tests;

/// <summary>
/// The HandleWebhook methods, given a WebhookModel as a JSON deserializer leaves it: with Data as a
/// JObject, which the handlers must deserialize again (a cast to the event's model would throw).
/// </summary>
public class WebhookHandlerTests
{
    private static readonly Guid DocumentId = Guid.NewGuid();

    [Theory]
    [InlineData(WebhookTypes.DocumentSigned, "signed")]
    [InlineData(WebhookTypes.DocumentApproved, "approved")]
    [InlineData(WebhookTypes.DocumentRefused, "refused")]
    [InlineData(WebhookTypes.DocumentConcluded, "concluded")]
    public void Document_event_is_reported_by_its_type(WebhookTypes type, string what)
    {
        var webhook = Deserialize($$"""{ "type": "{{type}}", "data": { "id": "{{DocumentId}}", "name": "sample.pdf" } }""");

        var output = Handle(new CheckDocumentStatusScenario(), webhook);

        Assert.Equal($"Document {DocumentId} is {what}!", output.Trim());
    }

    [Fact]
    public void Other_events_are_reported_as_unhandled_and_null_is_ignored_by_the_document_handler()
    {
        var created = Deserialize($$"""{ "type": "DocumentsCreated", "data": { "documents": [ { "id": "{{DocumentId}}" } ] } }""");

        Assert.Equal("This sample does not handle DocumentsCreated events", Handle(new CheckDocumentStatusScenario(), created).Trim());
        Assert.Empty(Handle(new CheckDocumentStatusScenario(), null));
    }

    [Fact]
    public void Closed_invoice_of_a_company_is_reported_with_its_totals_and_billing_information()
    {
        var invoice = new InvoiceClosedModel
        {
            Id = 42,
            Year = 2026,
            Month = 9,
            Value = 150.5m,
            Organization = new() { Name = "Sample Company" },
            InvoiceTotals = [new() { Total = 3, TransactionType = TransactionTypes.Signature, Value = 1.5m }],
            BillingInformation = new()
            {
                Type = BillingInformationTypes.Company,
                ContactName = "Jack Bauer",
                Email = "jack.bauer@mailinator.com",
                Company = new() { Name = "Sample Company", Identifier = "11222333000181" },
                City = "Brasilia",
                State = "DF",
            },
        };
        var webhook = Deserialize(JsonConvert.SerializeObject(new { type = "InvoiceClosed", data = invoice }, FakeSigner.JsonSettings));

        var output = Handle(new InvoiceWebhookHandlingScenario(), webhook);

        Assert.Contains("Invoice 42 for year 2026 and month 9 is closed", output);
        Assert.Contains("This is an organization invoice for Sample Company", output);
        Assert.Contains("3 transactions of type Signature", output);
        Assert.Contains("Company information: Sample Company - 11222333000181", output);
        Assert.Contains("Brasilia - DF", output);
    }

    // What an endpoint gets from deserializing the body: Data is a JObject.
    private static WebhookModel Deserialize(string body)
    {
        var webhook = JsonConvert.DeserializeObject<WebhookModel>(body)!;
        Assert.IsType<JObject>(webhook.Data);
        return webhook;
    }

    private static string Handle(IWebhookHandlerScenario handler, WebhookModel? webhook)
    {
        using var console = new ConsoleCapture();
        handler.HandleWebhook(webhook!);
        return console.Text;
    }
}
