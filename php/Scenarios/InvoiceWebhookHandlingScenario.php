<?php


namespace Lacuna\Scenarios;


use Lacuna\Signer\Model\BillingInformationTypes;
use Lacuna\Signer\Model\InvoicesInvoiceTotalModel;
use Lacuna\Signer\Model\InvoicesUpdateInvoicePaymentStatusRequest;
use Lacuna\Signer\Model\WebhooksInvoiceClosedModel;
use Lacuna\Signer\Model\WebhooksWebhookModel;
use Lacuna\Signer\Model\WebhookTypes;


class InvoiceWebhookHandlingScenario extends Scenario implements IWebhookHandlerScenario
{

    function HandleWebhook($webhook)
    {
        $webhook = new WebhooksWebhookModel(json_decode($webhook, true));
        if ($webhook != null) {
            if ($webhook->getType() == WebhookTypes::INVOICE_CLOSED) {
                $invoice = new WebhooksInvoiceClosedModel($webhook->getData());

                echo "Invoice " . $invoice->getId() . " for year " . $invoice->getYear() . " and month" . $invoice->getMonth() . "is closed";
                echo "The standing value is" . $invoice->getValue();

                if ($invoice->getOrganization()->getOwner() != null) {
                    echo "This is a personal account invoice for " . $invoice->getOrganization()->getOwner()->getName();
                } else {
                    echo "This is a personal account invoice for " . $invoice->getOrganization()->getName();
                }

                foreach ($invoice->getInvoiceTotals() as $item) {
                    $total = new InvoicesInvoiceTotalModel($item);
                    //check the total.Price property for additional info on the pricing for that type
                    echo $total->getTotal() . " transactions of type" . $total->getTransactionType() . "equals {total.Value}";
                }

                if ($invoice->getBillingInformation() != null) {
                    $info = $invoice->getBillingInformation();
                    echo "Contact information: " . $info->getContactName() . $info->getEmail() . $info->getPhone();

                    if ($info->getType() == BillingInformationTypes::INDIVIDUAL) {
                        echo "Individual information: " . $info->getIndividual()->getName() . " - " . $info->getIndividual()->getIdentifier();
                    } else {
                        echo "Company information: " . $info->getCompany()->getName() . " - " . $info->getCompany()->getIdentifier();
                    }

                    echo "Address: " . $info->getStreetAddress() . " - " . $info->getAddressNumber() . " - " . $info->getAdditionalAddressInfo();
                    echo $info->getNeighborhood() . " - " . $info->getZipCode();
                    echo $info->getCity() . " - " . $info->getState();
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
    ///
    function run()
    {
        $invoiceId = 3;//sample value, replace with actual ID
        $this->UpdateInvoiceStatus($invoiceId, true);
    }

    function UpdateInvoiceStatus($invoiceId, $isPaid)
    {
        $invoicePaymentStatusRequest = new InvoicesUpdateInvoicePaymentStatusRequest();
        $invoicePaymentStatusRequest->setIsPaid($isPaid);
        $this->signerClient->UpdateInvoiceStatus($invoiceId, $invoicePaymentStatusRequest);
    }
}