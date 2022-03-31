<?php


namespace Lacuna\Scenarios\DocumentGeneration;

use \ArrayAccess;
use \Lacuna\Signer\ObjectSerializer;
use \Lacuna\Signer\Model\ModelInterface;


class GenerationDocumentResult implements ModelInterface, ArrayAccess
{
    const DISCRIMINATOR = null;

    
    protected static $swaggerModelName = 'Documents.GenerationDocumentResult';

    
    protected static $swaggerTypes = [
        'folderId' => 'string',
'subscriptionId' => 'string',
'id' => 'string',
'type' => 'string',
'agentId' => 'string',
'totalDocumentsCount' => 'int',
'initializedCount' => 'int',
'completedCount' => 'int',
'status' => 'string'
];


    
    protected static $swaggerFormats = [
        'folderId' => null,
'subscriptionId' => null,
'id' => null,
'type' => null,
'agentId' => null,
'totalDocumentsCount' => null,
'initializedCount' => null,
'completedCount' => null,
'status' => null
];
        
   
    public static function swaggerTypes()
    {
        return self::$swaggerTypes;
    }

   
    public static function swaggerFormats()
    {
        return self::$swaggerFormats;
    }

   
    protected static $attributeMap = [
        'folderId' => 'folderId',
'subscriptionId' => 'subscriptionId',
'id' => 'id',
'type' => 'type',
'agentId' => 'agentId',
'totalDocumentsCount' => 'totalDocumentsCount',
'initializedCount' => 'initializedCount',
'completedCount' => 'completedCount',
'status' => 'status'    
];

    
    protected static $setters = [
        'folderId' => 'setFolderId',
'subscriptionId' => 'setSubscriptionId',
'id' => 'setId',
'type' => 'setType',
'agentId' => 'setAgentId',
'totalDocumentsCount' => 'setTotalDocumentsCount',
'initializedCount' => 'setInitializedCount',
'completedCount' => 'setCompletedCount',
'status' => 'setStatus'   
];

   
    protected static $getters = [
        'folderId' => 'getFolderId',
'subscriptionId' => 'getSubscriptionId',
'id' => 'getId',
'type' => 'getType',
'agentId' => 'getAgentId',
'totalDocumentsCount' => 'getTotalDocumentsCount',
'initializedCount' => 'getInitializedCount',
'completedCount' => 'getCompletedCount',
'status' => 'getStatus'    
 ];

    
    public static function attributeMap()
    {
        return self::$attributeMap;
    }

    
    public static function setters()
    {
        return self::$setters;
    }

   
    public static function getters()
    {
        return self::$getters;
    }

    
    public function getModelName()
    {
        return self::$swaggerModelName;
    }

    

    
    protected $container = [];

   
    public function __construct(array $data = null)
    {
        $this->container['folderId'] = isset($data['folderId']) ? $data['folderId'] : null;
        $this->container['subscriptionId'] = isset($data['subscriptionId']) ? $data['subscriptionId'] : null;
        $this->container['id'] = isset($data['id']) ? $data['id'] : null;
        $this->container['type'] = isset($data['type']) ? $data['type'] : null;
        $this->container['agentId'] = isset($data['agentId']) ? $data['agentId'] : null;
        $this->container['totalDocumentsCount'] = isset($data['totalDocumentsCount']) ? $data['totalDocumentsCount'] : null;
        $this->container['initializedCount'] = isset($data['initializedCount']) ? $data['initializedCount'] : null;
        $this->container['completedCount'] = isset($data['completedCount']) ? $data['completedCount'] : null;
        $this->container['status'] = isset($data['status']) ? $data['status'] : null;
    }

   
    public function listInvalidProperties()
    {
        $invalidProperties = [];

        return $invalidProperties;
    }

    
    public function valid()
    {
        return count($this->listInvalidProperties()) === 0;
    }


    
    public function getFolderId()
    {
        return $this->container['folderId'];
    }

    
    public function setFolderId($folderId)
    {
        $this->container['folderId'] = $folderId;

        return $this;
    }

   
    public function getSubscriptionId()
    {
        return $this->container['subscriptionId'];
    }

   
    public function setSubscriptionId($subscriptionId)
    {
        $this->container['subscriptionId'] = $subscriptionId;

        return $this;
    }

   
    public function getId()
    {
        return $this->container['id'];
    }

    
    public function setId($id)
    {
        $this->container['id'] = $id;

        return $this;
    }


    public function getType()
    {
        return $this->container['type'];
    }

   
    public function setType($type)
    {
        $this->container['type'] = $type;

        return $this;
    }

    

    public function getAgentId()
    {
        return $this->container['agentId'];
    }

   
    public function setAgentId($agentId)
    {
        $this->container['agentId'] = $agentId;

        return $this;
    }

   
    public function getTotalDocumentsCount()
    {
        return $this->container['totalDocumentsCount'];
    }

    
    public function setTotalDocumentsCount($totalDocumentsCount)
    {
        $this->container['totalDocumentsCount'] = $totalDocumentsCount;

        return $this;
    }

    
   
    public function getInitializedCount()
    {
        return $this->container['initializedCount'];
    }

    
    public function setInitializedCount($initializedCount)
    {
        $this->container['initializedCount'] = $initializedCount;

        return $this;
    }

    
    
    public function getCompletedCount()
    {
        return $this->container['completedCount'];
    }

   
    public function setCompletedCount($completedCount)
    {
        $this->container['completedCount'] = $completedCount;

        return $this;
    }

    
   
    public function getStatus()
    {
        return $this->container['status'];
    }

   
    public function setStatus($status)
    {
        $this->container['status'] = $status;

        return $this;
    }


   
    public function offsetExists($offset)
    {
        return isset($this->container[$offset]);
    }

   
    public function offsetGet($offset)
    {
        return isset($this->container[$offset]) ? $this->container[$offset] : null;
    }

   
    public function offsetSet($offset, $value)
    {
        if (is_null($offset)) {
            $this->container[] = $value;
        } else {
            $this->container[$offset] = $value;
        }
    }

  
    public function offsetUnset($offset)
    {
        unset($this->container[$offset]);
    }

  
    public function __toString()
    {
        if (defined('JSON_PRETTY_PRINT')) { // use JSON pretty print
            return json_encode(
                ObjectSerializer::sanitizeForSerialization($this),
                JSON_PRETTY_PRINT
            );
        }

        return json_encode(ObjectSerializer::sanitizeForSerialization($this));
    }
}
