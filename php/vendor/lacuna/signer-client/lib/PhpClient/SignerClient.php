<?php

namespace Lacuna\Signer\PhpClient;

use Exception;
use Lacuna\Signer\Model\DocumentsCancelDocumentRequest;
use Lacuna\Signer\Model\DocumentsCreateDocumentResult;
use Lacuna\Signer\Model\DocumentsDocumentAddVersionRequest;
use Lacuna\Signer\Model\DocumentsDocumentListModel;
use Lacuna\Signer\Model\FoldersFolderInfoModel;
use Lacuna\Signer\Model\NotificationsCreateFlowActionReminderRequest;
use Lacuna\Signer\Model\PaginatedSearchResponseDocumentsDocumentListModel;
use Lacuna\Signer\Model\PaginatedSearchResponseFoldersFolderInfoModel;
use Lacuna\Signer\Model\RefusalRefusalRequest;
use Lacuna\Signer\Model\DocumentGenerateResult;
use Lacuna\Signer\Model\DocumentGenerationModel;
use Lacuna\Signer\Model\TicketModel;
use Lacuna\Signer\PhpClient\Params\DocumentListParameters;
use Lacuna\Signer\PhpClient\Params\PaginatedSearchParams;
use Lacuna\Signer\PhpClient\RestClient;
use ReflectionClass;
use ReflectionProperty;


/**
 * Class SignerClient
 * @package Lacuna\Signer\PhpClient
 */
class SignerClient
{
    protected $apiKey;
    protected $endpointUri;
    protected $restClient;

    /**
     * @param string $endpointUri
     * @param string $apiKey
     * @throws Exception
     */
    public function __construct($endpointUri, $apiKey)
    {
        $this->endpointUri = $endpointUri;
        $this->apiKey = $apiKey;
    }

    /**
     * @return RestClient
     */
    function getRestClient()
    {
        if ($this->restClient == null) {
            $this->restClient = new RestClient($this->endpointUri, $this->apiKey);
        }

        return $this->restClient;

    }

    /**
     * @param string $request
     * @return DocumentsCreateDocumentResult[]
     * @throws Exception
     */
    function createDocument($request)
    {
        $response = $this->getRestClient()->post("/api/documents", $request);
        $result = array();
        foreach ($response as $element) {
            array_push($result, new DocumentsCreateDocumentResult($element));
        }
        return $result;
    }


    /**
     * @param string $id
     * @param RefusalRefusalRequest $request
     * @throws Exception
     */
    function refuseDocument($id, $request)
    {
        $requestUri = "/api/documents/" . $id . "/refusal";
        $this->getRestClient()->post($requestUri, $request);

    }

    /**
     * @param string $id
     * @param DocumentsCancelDocumentRequest $request
     * @throws Exception
     */
    function cancelDocument($id, $request)
    {
        $requestUri = "/api/documents/" . $id . "/cancellation";
        $this->getRestClient()->post($requestUri, $request);

    }

    /**
     * @param string $id
     * @param DocumentsDocumentAddVersionRequest $request
     * @throws Exception
     */
    function addNewDocumentVersion($id, $request)
    {
        $requestUri = "/api/documents/" . $id . "/versions";
        $this->getRestClient()->post($requestUri, $request);

    }

    /**
     * @param string $id
     * @throws Exception
     */
    function getDocumentDetails($id)
    {
        $requestUri = "/api/documents/" . $id;
        $restClient = $this->getRestClient();
        $result = $restClient->get($requestUri);

        return $result;
    }

    /**
     * @throws Exception
     */
    function getDocumentDownloadTicket($id, $type)
    {
        $requestUri = "/api/documents/" . $id . "/ticket?type=" . $type;
        $ticket = new TicketModel($this->getRestClient()->get($requestUri));
        return $ticket;
    }

    /**
     * @throws Exception
     */
    function getDocumentContent($id, $type)
    {
        $requestUri = "/api/documents/" . $id . "/content?type=" . $type;
        $document = $this->getRestClient()->getStream($requestUri);
        return $document;
    }


    /**
     * @throws Exception
     */
    function UpdateInvoiceStatus($id, $request) {
        $requestUri = "/api/invoices/" . $id ."/payment";
        return $this->getRestClient()->put($requestUri, $request);
    }

    /**
     * @throws Exception
     */
    function GetDocumentContentBytesAsync($id, $type)
    {
        $requestUri = "/api/documents/" . $id . "/content-b64?type=" . $type;
        $document = $this->getRestClient()->get($requestUri);
        return $document;
    }

    /**
     * @param string $name
     * @param string $mimeType
     * @throws \GuzzleHttp\Exception\GuzzleException
     */
    function uploadFile($name, $file, $mimeType)
    {
        $requestUri = "/api/uploads";
        $restClient = $this->getRestClient();

        $result = $restClient->postMultiPart($requestUri, $file, $name, $mimeType);

        return $result;
    }

    /**
     * @param string $id
     * @throws Exception
     */
    function deleteDocument($id)
    {
        $requestUri = "/api/documents/" . $id;
        $restClient = $this->getRestClient();
        $restClient->delete($requestUri);
    }

    /**
     * @return  PaginatedSearchResponseFoldersFolderInfoModel
     * @throws \Exception
     */
    function listFoldersPaginated($searchParams, $organizationId)
    {
        $requestUri = "/api/folders" . $this->buildSearchPaginatedParamsString($searchParams) . "&organizationId=" . $this->getParameterOrEmpty($organizationId);
        $response = $this->getRestClient()->get($requestUri);

        $result = array();

        foreach ($response["items"] as $element) {
            array_push($result, new FoldersFolderInfoModel($element));
        }

        $model = new PaginatedSearchResponseFoldersFolderInfoModel($response);
        $model->setItems($result);
        $model->setTotalCount($response["totalCount"]);

        return $model;
    }

    /**
     * @return PaginatedSearchResponseDocumentsDocumentListModel
     * @throws \Exception
     */
    function listDocuments($searchParams)
    {
        $requestUri = "/api/documents?" . $this->buildSearchDocumentListString($searchParams);
        $response = $this->getRestClient()->get($requestUri);

        $result = array();

        foreach ($response["items"] as $element) {
            array_push($result, new DocumentsDocumentListModel($element));
        }

        $model = new PaginatedSearchResponseDocumentsDocumentListModel();
        $model->setItems($result);
        $model->setTotalCount($response["totalCount"]);

        return $model;
    }

    function getActionUrl($documentId, $request)
    {
        $requestUri = "/api/documents/" . $documentId . "/action-url";

        $response = $this->getRestClient()->post($requestUri, $request);

        return $response;
    }
  
    function generateDocument($request)
    {
        $response = $this->getRestClient()->post("/api/documents/generation", $request);
        $result = new DocumentGenerationModel($response);
       
        return $result;
    }
    
    function getGenerationStatus($id)
    {
        $response = $this->getRestClient()->get("/api/documents/generation/{$id}");
        $result = new DocumentGenerationModel($response);
       
        return $result;
    }
 

    // region NOTIFICATIONS

    /**
     * @throws Exception
     */
    function sendFlowActionReminder($documentId, $flowActionId)
    {
        $request = new NotificationsCreateFlowActionReminderRequest();
        $request->setDocumentId($documentId);
        $request->setFlowActionId($flowActionId);
        $this->getRestClient()->post("/api/notifications/flow-action-reminder", $request);
    }

    // endregion

    /**
     * @param PaginatedSearchParams $searchParams
     * @throws Exception
     */
    function buildSearchPaginatedParamsString($searchParams)
    {
        return "?q=" . $this->getParameterOrEmpty($searchParams->getQ()) . "&limit=" . $searchParams->getLimit() . "&offset=" . $this->getParameterOrZero($searchParams->getOffset());
    }

    /**
     * @param DocumentListParameters $searchParams
     * @throws Exception
     */
    function buildSearchDocumentListString($searchParams)
    {
        return "IsConcluded=" . json_encode($searchParams->getIsConcluded()) . "&OrganizationType=Normal&FolderType=Normal&FilterByDocumentType=False&Q=" . $this->getParameterOrEmpty($searchParams->getQ()) . "&Limit=" . $searchParams->getLimit() . "&Offset=0&Order=" . $searchParams->getOrder();
    }


    function getParameterOrEmpty($parameter)
    {
        if ($parameter == null || strlen($parameter) == 0) {
            return "";
        }

        try {
            return urlencode($parameter);
        } catch (Exception $e) {
            return "";

        }
    }


    function getParameterOrZero($parameter)
    {
        if ($parameter == null || strlen($parameter) == 0) {
            return "0";
        }

        try {
            return urlencode($parameter);
        } catch (Exception $e) {
            return "";

        }
    }

    function objectToArray($object)
    {
        if (!is_object($object) && !is_array($object))
            return $object;

        return array_map('objectToArray', (array)$object);
    }
}