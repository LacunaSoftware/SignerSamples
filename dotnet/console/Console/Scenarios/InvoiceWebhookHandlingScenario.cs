using Lacuna.Signer.Api;
using Lacuna.Signer.Api.Invoices;
using Lacuna.Signer.Api.Webhooks;
using Lacuna.Signer.Client;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Console.Scenarios
{
    public class InvoiceWebhookHandlingScenario : IWebhookHandlerScenario
    {
        protected SignerClient SignerClient;

        public InvoiceWebhookHandlingScenario()
        {
            // Homologation instance
            var domain = "https://signer-lac.azurewebsites.net";
            // Application credentials token.
            var token = "API Sample App|43fc0da834e48b4b840fd6e8c37196cf29f919e5daedba0f1a5ec17406c13a99";
            SignerClient = new SignerClient(domain, token);
        }

        public void HandleWebhook(WebhookModel webhook)
        {
            if (webhook != null)
            {
                if (webhook.Type == WebhookTypes.InvoiceClosed)
                {
                    var invoice = (InvoiceClosedModel)webhook.Data;

                    System.Console.WriteLine($"Invoice {invoice.Id} for year {invoice.Year} and month {invoice.Month} is closed");
                    System.Console.WriteLine($"The standing value is {invoice.Value}");

                    if (invoice.Organization.Owner != null)
                    {
                        System.Console.WriteLine($"This is a personal account invoice for {invoice.Organization.Owner.Name}");
                    } else
                    {
                        System.Console.WriteLine($"This is an organization invoice for {invoice.Organization.Name}");
                    }

                    foreach (var total in invoice.InvoiceTotals)
                    {
                        //check the total.Price property for additional info on the pricing for that type
                        System.Console.WriteLine($"{total.Total} transactions of type {total.TransactionType} equals {total.Value}");
                    }
                }
            }
        }

        protected async Task UpdateInvoiceStatusAsync(int invoiceId, bool isPaid)
        {
            var invoicePaymentStatusRequest = new UpdateInvoicePaymentStatusRequest { IsPaid = isPaid };
            await SignerClient.UpdateInvoiceStatusAsync(invoiceId, invoicePaymentStatusRequest);
        }
    }
}
