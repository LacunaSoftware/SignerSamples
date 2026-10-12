using Lacuna.Signer.Api;
using Lacuna.Signer.Api.Invoices;
using Lacuna.Signer.Api.Webhooks;
using Lacuna.Signer.Client;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Console.Scenarios
{
    public class InvoiceWebhookHandlingScenario : Scenario, IWebhookHandlerScenario
    {
        public void HandleWebhook(WebhookModel webhook)
        {
            if (webhook != null)
            {
                if (webhook.Type == WebhookTypes.InvoiceClosed)
                {
                    // WebhookModel.Data is an object: a JSON deserializer leaves it as a JObject (or a
                    // JsonElement), never an InvoiceClosedModel, so a cast would throw. Deserialize it again.
                    var invoice = JsonConvert.DeserializeObject<InvoiceClosedModel>(webhook.Data.ToString());

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

                    if (invoice.BillingInformation != null)
                    {
                        var info = invoice.BillingInformation;
                        System.Console.WriteLine($"Contact information: {info.ContactName} - {info.Email} - {info.Phone}");

                        if (info.Type == BillingInformationTypes.Individual)
                        {
                            System.Console.WriteLine($"Individual information: {info.Individual.Name} - {info.Individual.Identifier}");
                        } else
                        {
                            System.Console.WriteLine($"Company information: {info.Company.Name} - {info.Company.Identifier}");
                        }

                        System.Console.WriteLine($"Address: {info.StreetAddress} - {info.AddressNumber} - {info.AdditionalAddressInfo}");
                        System.Console.WriteLine($"{info.Neighborhood} - {info.ZipCode}");
                        System.Console.WriteLine($"{info.City} - {info.State}");
                    }
                }
            }
        }

        /// <summary>
        /// After receiving the Webhook, when the invoice payment is detected, update the invoice to mark it as paid.
        /// 
        /// This requires a token with billing administration privileges.
        /// </summary>
        /// <returns></returns>
        public override async Task RunAsync()
        {
            var invoiceId = 1;//sample value, replace with actual ID
            await UpdateInvoiceStatusAsync(invoiceId, true);
        }

        protected async Task UpdateInvoiceStatusAsync(int invoiceId, bool isPaid)
        {
            var invoicePaymentStatusRequest = new UpdateInvoicePaymentStatusRequest { IsPaid = isPaid };
            await SignerClient.UpdateInvoiceStatusAsync(invoiceId, invoicePaymentStatusRequest);
        }
    }
}
