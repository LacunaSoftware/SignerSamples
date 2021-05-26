<?php


namespace Lacuna\Scenarios;


use Lacuna\Signer\Model\PaginatedSearchResponseDocumentsDocumentListModel;
use Lacuna\Signer\Model\PaginationOrders;
use Lacuna\Signer\PhpClient\Params\DocumentListParameters;

class ListDocumentsScenario extends Scenario
{
    /**
     * This scenario describes how to list documents.
     */
    function run()
    {
        //1 - define the arguments to list the documents according to your requirements
        $searchParams = new DocumentListParameters();
        $searchParams->setOrder(PaginationOrders::DESC);
        $searchParams->setLimit(20);
        $searchParams->setIsConcluded(false);
        $searchParams->setQ("One Signer Sample");

        //2 - Call the ListDocuments method and pass "searchParams" as a parameter
        $response = $this->signerClient->listDocuments($searchParams);

        foreach ($response->getItems() as $item) {
            echo $item->getName() . "\n";
        }

    }

}