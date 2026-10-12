<?php


namespace Lacuna\Scenarios;


use Lacuna\Signer\Model\BillingBillingInformationModel;
use Lacuna\Signer\Model\BillingCompanyBillingInformationModel;
use Lacuna\Signer\Model\BillingIndividualBillingInformationModel;
use Lacuna\Signer\Model\BillingInformationTypes;
use Lacuna\Signer\Model\InvoicesInvoiceTotalModel;
use Lacuna\Signer\Model\InvoicesUpdateInvoicePaymentStatusRequest;
use Lacuna\Signer\Model\OrganizationsOrganizationInfoModel;
use Lacuna\Signer\Model\OrganizationsOrganizationOwnerInfoModel;
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

                // The client's models keep nested objects as plain arrays, so wrap each one in its model
                // before calling its getters.
                echo "Invoice " . $invoice->getId() . " for " . $invoice->getYear() . "-" . $invoice->getMonth() . " is closed\n";
                echo "The standing value is " . $invoice->getValue() . "\n";

                $organization = new OrganizationsOrganizationInfoModel($invoice->getOrganization());
                if ($organization->getOwner() != null) {
                    $owner = new OrganizationsOrganizationOwnerInfoModel($organization->getOwner());
                    echo "This is a personal account invoice for " . $owner->getName() . "\n";
                } else {
                    echo "This is an organization invoice for " . $organization->getName() . "\n";
                }

                foreach ($invoice->getInvoiceTotals() as $item) {
                    $total = new InvoicesInvoiceTotalModel($item);
                    // Check the price property for additional info on the pricing for that type
                    echo $total->getTotal() . " transactions of type " . $total->getTransactionType() . " equals " . $total->getValue() . "\n";
                }

                if ($invoice->getBillingInformation() != null) {
                    $info = new BillingBillingInformationModel($invoice->getBillingInformation());
                    echo "Contact information: " . $info->getContactName() . " - " . $info->getEmail() . " - " . $info->getPhone() . "\n";

                    if ($info->getType() == BillingInformationTypes::INDIVIDUAL) {
                        $individual = new BillingIndividualBillingInformationModel($info->getIndividual());
                        echo "Individual information: " . $individual->getName() . " - " . $individual->getIdentifier() . "\n";
                    } else {
                        $company = new BillingCompanyBillingInformationModel($info->getCompany());
                        echo "Company information: " . $company->getName() . " - " . $company->getIdentifier() . "\n";
                    }

                    echo "Address: " . $info->getStreetAddress() . " - " . $info->getAddressNumber() . " - " . $info->getAdditionalAddressInfo() . "\n";
                    echo $info->getNeighborhood() . " - " . $info->getZipCode() . "\n";
                    echo $info->getCity() . " - " . $info->getState() . "\n";
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
        var_dump($this->signerClient->UpdateInvoiceStatus($invoiceId, $invoicePaymentStatusRequest));
    }
}