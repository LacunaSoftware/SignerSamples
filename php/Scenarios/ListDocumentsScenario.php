<?php


namespace Lacuna\Scenarios;


use Lacuna\Signer\Model\PaginatedSearchResponseDocumentsDocumentListModel;
use Lacuna\Signer\Model\PaginationOrders;
use Lacuna\Signer\PhpClient\Params\DocumentListParameters;

class ListDocumentsScenario extends Scenario
{
    function run()
    {
        $searchParams = new DocumentListParameters();
        $searchParams->setOrder(PaginationOrders::DESC);
        $searchParams->setLimit(20);
        $searchParams->setIsConcluded(false);
        $searchParams->setQ("One Signer Sample");

        $result = new PaginatedSearchResponseDocumentsDocumentListModel($this->signerClient->listDocuments($searchParams));


        foreach ($result->getItems() as $item) {
            echo $item['name'] . "\n";
        }

    }

}