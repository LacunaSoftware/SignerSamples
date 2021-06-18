package com.lacunasoftware.signer.sample.scenarios;

import com.lacunasoftware.signer.BillingInformationTypes;
import com.lacunasoftware.signer.InvoicesUpdateInvoicePaymentStatusRequest;
import com.lacunasoftware.signer.WebhookTypes;
import com.lacunasoftware.signer.billing.BillingInformationModel;
import com.lacunasoftware.signer.invoices.InvoiceTotalModel;
import com.lacunasoftware.signer.javaclient.exceptions.RestException;
import com.lacunasoftware.signer.webhooks.InvoiceClosedModel;
import com.lacunasoftware.signer.webhooks.WebhookModel;

import java.io.IOException;

public class InvoiceWebhookHandlingScenario extends Scenario implements IWebhookHandlerScenario {
    @Override
    public void HandleWebhook(WebhookModel webhook) {

        if (webhook != null)
        {
            if (webhook.getType() == WebhookTypes.INVOICECLOSED)
            {
                InvoiceClosedModel invoice = (InvoiceClosedModel)webhook.getData();

                System.out.println(String.format("Invoice %s for year %s and month %s is closed", invoice.getId().toString(), invoice.getYear(), invoice.getMonth()));
                System.out.println(String.format("The standing value is %s", invoice.getValue()));

                if (invoice.getOrganization().getOwner() != null)
                {
                    System.out.println(String.format("This is a personal account invoice for %s", invoice.getOrganization().getOwner().getName()));
                } else
                {
                    System.out.println(String.format("This is an organization invoice for %s", invoice.getOrganization().getName()));
                }

                for (InvoiceTotalModel total : invoice.getInvoiceTotals()) {
                    //check the total.Price property for additional info on the pricing for that type
                    System.out.println(String.format("%s transactions of type %s equals %s", total.getTotal(), total.getTransactionType(), total.getValue()));
                }


                if (invoice.getBillingInformation() != null) {

                    BillingInformationModel info = invoice.getBillingInformation();
                    System.out.println(String.format("Contact information: %s - %s - %s", info.getContactName(), info.getEmail(), info.getPhone()));

                    if (info.getType() == BillingInformationTypes.INDIVIDUAL)
                    {
                        System.out.println(String.format("Individual information: %s - %s", info.getIndividual().getName(), info.getIndividual().getIdentifier()));
                    } else
                    {
                        System.out.println(String.format("Company information: %s - %s", info.getCompany().getName(), info.getCompany().getIdentifier()));
                    }

                    System.out.println(String.format("Address: %s - %s - %s", info.getStreetAddress(), info.getAddressNumber(), info.getAdditionalAddressInfo()));
                    System.out.println(String.format("%s - %s", info.getNeighborhood(), info.getZipCode()));
                    System.out.println(String.format("%s - %s", info.getCity(), info.getState()));
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
    @Override
    public void Run() throws Exception {
        int invoiceId = 16;//sample value, replace with actual ID
        this.UpdateInvoiceStatus(invoiceId, true);

    }


    protected void UpdateInvoiceStatus(int invoiceId, boolean isPaid) throws IOException {
        InvoicesUpdateInvoicePaymentStatusRequest invoicePaymentStatusRequest = new InvoicesUpdateInvoicePaymentStatusRequest().isPaid(isPaid);
        try {
            this.signerClient.UpdateInvoiceStatus(invoiceId, invoicePaymentStatusRequest);
        }catch (RestException e){
            System.out.println(e.getMessage());
        }
    }
}
