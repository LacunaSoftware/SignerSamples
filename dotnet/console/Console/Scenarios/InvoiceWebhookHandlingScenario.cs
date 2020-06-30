using Lacuna.Signer.Api;
using Lacuna.Signer.Api.Webhooks;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Console.Scenarios
{
    public class InvoiceWebhookHandlingScenario : IWebhookHandlerScenario
    {
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
    }
}
