<?php

namespace Lacuna\Signer\Model;

use \ArrayAccess;
use \Lacuna\Signer\ObjectSerializer;

class DocumentGenerationDocumentModel implements ModelInterface, ArrayAccess
{
    const DISCRIMINATOR = null;

    /**
      * The original name of the model.
      *
      * @var string
      */
    protected static $swaggerModelName = 'Documents.CreateDocumentRequest';

    /**
      * Array of property to type mappings. Used for (de)serialization
      *
      * @var string[]
      */
    protected static $swaggerTypes = [
        'flowActions' => '\Lacuna\Signer\Model\FlowActionsFlowActionCreateModel[]',
'observers' => '\Lacuna\Signer\Model\ObserversObserverCreateModel[]',
'folderId' => 'string',
'newFolderName' => 'string',
'organizationId' => 'string',
'type' => 'string',
'notaryType' => '\Lacuna\Signer\Model\NotaryTypes',
'expirationDate' => '\DateTime',    
'notifiedEmails' => 'string[]',
'dataFile' => '\Lacuna\Signer\Model\AttachmentsAttachmentUploadModel[]',
'templateFile' => '\Lacuna\Signer\Model\AttachmentsAttachmentUploadModel[]'
];

  /**
      * Array of property to format mappings. Used for (de)serialization
      *
      * @var string[]
      */
      protected static $swaggerFormats = [
//         'files' => null,
// 'attachments' => null,
// 'xmlNamespaces' => null,
// 'isEnvelope' => null,
// 'envelopeName' => null,
// 'folderId' => 'uuid',
// 'description' => null,
// 'flowActions' => null,
// 'observers' => null,
// 'disablePendingActionNotifications' => null,
// 'newFolderName' => null,
// 'forceCadesSignature' => null,
// 'notifiedEmails' => null,
// 'expirationDate' => 'date-time'    
'flowActions' => 'null',
'observers' => 'null',
'folderId' => 'null',
'newFolderName' => 'null',
'organizationId' => 'null',
'type' => 'null',
'notaryType' => 'null',
'expirationDate' => 'null',    
'notifiedEmails' => 'null',
'dataFile' => 'null',
'templateFile' => 'null'
];

 /**
     * Array of property to type mappings. Used for (de)serialization
     *
     * @return array
     */
    public static function swaggerTypes()
    {
        return self::$swaggerTypes;
    }

    /**
     * Array of property to format mappings. Used for (de)serialization
     *
     * @return array
     */
    public static function swaggerFormats()
    {
        return self::$swaggerFormats;
    }

    /**
     * Array of attributes where the key is the local name,
     * and the value is the original name
     *
     * @var string[]
     */
    protected static $attributeMap = [
//         'files' => 'files',
// 'attachments' => 'attachments',
// 'xmlNamespaces' => 'xmlNamespaces',
// 'isEnvelope' => 'isEnvelope',
// 'envelopeName' => 'envelopeName',
// 'folderId' => 'folderId',
// 'description' => 'description',
// 'flowActions' => 'flowActions',
// 'observers' => 'observers',
// 'disablePendingActionNotifications' => 'disablePendingActionNotifications',
// 'newFolderName' => 'newFolderName',
// 'forceCadesSignature' => 'forceCadesSignature',
// 'notifiedEmails' => 'notifiedEmails',
// 'expirationDate' => 'expirationDate'
'flowActions' => 'flowActions',
'observers' => 'observers',
'folderId' => 'folderId',
'newFolderName' => 'newFolderName',
'organizationId' => 'organizationId',
'type' => 'type',
'notaryType' => 'notaryType',
'expirationDate' => 'expirationDate',    
'notifiedEmails' => 'notifiedEmails',
'dataFile' => 'dataFile',
'templateFile' => 'templateFile'    
];

    /**
     * Array of attributes to setter functions (for deserialization of responses)
     *
     * @var string[]
     */
    protected static $setters = [
//         'files' => 'setFiles',
// 'attachments' => 'setAttachments',
// 'xmlNamespaces' => 'setXmlNamespaces',
// 'isEnvelope' => 'setIsEnvelope',
// 'envelopeName' => 'setEnvelopeName',
// 'folderId' => 'setFolderId',
// 'description' => 'setDescription',
// 'flowActions' => 'setFlowActions',
// 'observers' => 'setObservers',
// 'disablePendingActionNotifications' => 'setDisablePendingActionNotifications',
// 'newFolderName' => 'setNewFolderName',
// 'forceCadesSignature' => 'setForceCadesSignature',
// 'notifiedEmails' => 'setNotifiedEmails',
// 'expirationDate' => 'setExpirationDate'    
'flowActions' => 'setFlowActions',
'observers' => 'setObservers',
'folderId' => 'setFolderId',
'newFolderName' => 'setNewFolderName',
'organizationId' => 'setOrganizationId',
'type' => 'setType',
'notaryType' => 'setNotaryType',
'expirationDate' => 'setExpirationDate',    
'notifiedEmails' => 'setNotifiedEmails',
'dataFile' => 'setDataFile',
'templateFile' => 'setTemplateFile'   
];

    /**
     * Array of attributes to getter functions (for serialization of requests)
     *
     * @var string[]
     */
    protected static $getters = [
//         'files' => 'getFiles',
// 'attachments' => 'getAttachments',
// 'xmlNamespaces' => 'getXmlNamespaces',
// 'isEnvelope' => 'getIsEnvelope',
// 'envelopeName' => 'getEnvelopeName',
// 'folderId' => 'getFolderId',
// 'description' => 'getDescription',
// 'flowActions' => 'getFlowActions',
// 'observers' => 'getObservers',
// 'disablePendingActionNotifications' => 'getDisablePendingActionNotifications',
// 'newFolderName' => 'getNewFolderName',
// 'forceCadesSignature' => 'getForceCadesSignature',
// 'notifiedEmails' => 'getNotifiedEmails',
// 'expirationDate' => 'getExpirationDate'    
'flowActions' => 'getFlowActions',
'observers' => 'getObservers',
'folderId' => 'getFolderId',
'newFolderName' => 'getNewFolderName',
'organizationId' => 'getOrganizationId',
'type' => 'getType',
'notaryType' => 'getNotaryType',
'expirationDate' => 'getExpirationDate',    
'notifiedEmails' => 'getNotifiedEmails',
'dataFile' => 'getDataFile',
'templateFile' => 'getTemplateFile'   
];

    /**
     * Array of attributes where the key is the local name,
     * and the value is the original name
     *
     * @return array
     */
    public static function attributeMap()
    {
        return self::$attributeMap;
    }

    /**
     * Array of attributes to setter functions (for deserialization of responses)
     *
     * @return array
     */
    public static function setters()
    {
        return self::$setters;
    }

    /**
     * Array of attributes to getter functions (for serialization of requests)
     *
     * @return array
     */
    public static function getters()
    {
        return self::$getters;
    }

    /**
     * The original name of the model.
     *
     * @return string
     */
    public function getModelName()
    {
        return self::$swaggerModelName;
    }

    

    /**
     * Associative array for storing property values
     *
     * @var mixed[]
     */
    protected $container = [];

    /**
     * Constructor
     *
     * @param mixed[] $data Associated array of property values
     *                      initializing the model
     */
    public function __construct(array $data = null)
    {
        // $this->container['files'] = isset($data['files']) ? $data['files'] : null;
        // $this->container['attachments'] = isset($data['attachments']) ? $data['attachments'] : null;
        // $this->container['xmlNamespaces'] = isset($data['xmlNamespaces']) ? $data['xmlNamespaces'] : null;
        // $this->container['isEnvelope'] = isset($data['isEnvelope']) ? $data['isEnvelope'] : null;
        // $this->container['envelopeName'] = isset($data['envelopeName']) ? $data['envelopeName'] : null;
        // $this->container['folderId'] = isset($data['folderId']) ? $data['folderId'] : null;
        // $this->container['description'] = isset($data['description']) ? $data['description'] : null;
        // $this->container['flowActions'] = isset($data['flowActions']) ? $data['flowActions'] : null;
        // $this->container['observers'] = isset($data['observers']) ? $data['observers'] : null;
        // $this->container['disablePendingActionNotifications'] = isset($data['disablePendingActionNotifications']) ? $data['disablePendingActionNotifications'] : null;
        // $this->container['newFolderName'] = isset($data['newFolderName']) ? $data['newFolderName'] : null;
        // $this->container['forceCadesSignature'] = isset($data['forceCadesSignature']) ? $data['forceCadesSignature'] : null;
        // $this->container['notifiedEmails'] = isset($data['notifiedEmails']) ? $data['notifiedEmails'] : null;
        // $this->container['expirationDate'] = isset($data['expirationDate']) ? $data['expirationDate'] : null;
        $this->container['flowActions'] = isset($data['flowActions']) ? $data['flowActions'] : null;
        $this->container['observers'] = isset($data['observers']) ? $data['observers'] : null;
        $this->container['folderId'] = isset($data['folderId']) ? $data['folderId'] : null;
        $this->container['newFolderName'] = isset($data['newFolderName']) ? $data['newFolderName'] : null;
        $this->container['organizationId'] = isset($data['organizationId']) ? $data['organizationId'] : null;
        $this->container['type'] = isset($data['type']) ? $data['type'] : null;
        $this->container['notaryType'] = isset($data['notaryType']) ? $data['notaryType'] : null;
        $this->container['expirationDate'] = isset($data['expirationDate']) ? $data['expirationDate'] : null;
        $this->container['notifiedEmails'] = isset($data['notifiedEmails']) ? $data['notifiedEmails'] : null;
        $this->container['dataFile'] = isset($data['dataFile']) ? $data['dataFile'] : null;
        $this->container['templateFile'] = isset($data['templateFile']) ? $data['templateFile'] : null;
        
    }

    /**
     * Show all the invalid properties with reasons.
     *
     * @return array invalid properties with reasons
     */
    public function listInvalidProperties()
    {
        $invalidProperties = [];

        if ($this->container['dataFile'] === null) {
            $invalidProperties[] = "'dataFile' can't be null";
        }
        if ($this->container['templateFile'] === null) {
            $invalidProperties[] = "'templateFile' can't be null";
        }
        if ($this->container['flowActions'] === null) {
            $invalidProperties[] = "'flowActions' can't be null";
        }
        return $invalidProperties;
    }

    /**
     * Validate all the properties in the model
     * return true if all passed
     *
     * @return bool True if all properties are valid
     */
    public function valid()
    {
        return count($this->listInvalidProperties()) === 0;
    }
    
    /**
     * Gets flowActions
     *
     * @return \Lacuna\Signer\Model\FlowActionsFlowActionCreateModel[]
     */
    public function getFlowActions()
    {
        return $this->container['flowActions'];
    }

    /**
     * Sets flowActions
     *
     * @param \Lacuna\Signer\Model\FlowActionsFlowActionCreateModel[] $flowActions The list of actions (signers and approvers) that will be in the document.
     *
     * @return $this
     */
    public function setFlowActions($flowActions)
    {
        $this->container['flowActions'] = $flowActions;
        
        return $this;
    }
    
    /**
     * Gets observers
     *
     * @return \Lacuna\Signer\Model\ObserversObserverCreateModel[]
     */
    public function getObservers()
    {
        return $this->container['observers'];
    }

    /**
     * Sets observers
     *
     * @param \Lacuna\Signer\Model\ObserversObserverCreateModel[] $observers observers
     *
     * @return $this
     */
    public function setObservers($observers)
    {
        $this->container['observers'] = $observers;

        return $this;
    }

    /**
     * Gets folderId
     *
     * @return string
     */
    public function getFolderId()
    {
        return $this->container['folderId'];
    }

    /**
     * Sets folderId
     *
     * @param string $folderId The id of the folder in which the document should be placed or null if it should not be placed in any specific folder.
     *
     * @return $this
     */
    public function setFolderId($folderId)
    {
        $this->container['folderId'] = $folderId;

        return $this;
    }

    
    /**
     * Gets newFolderName
     *
     * @return string
     */
    public function getNewFolderName()
    {
        return $this->container['newFolderName'];
    }

    /**
     * Sets newFolderName
     *
     * @param string $newFolderName The name of a new folder to be created and associated to the document. If you do not wish to create a new folder you may set this as null.
     *
     * @return $this
     */
    public function setNewFolderName($newFolderName)
    {
        $this->container['newFolderName'] = $newFolderName;

        return $this;
    }

    /**
     * Gets organizationId
     *
     * @return string
     */
    public function getOrganizationId()
    {
        return $this->container['organizationId'];
    }

    /**
     * Sets organizationId
     *
     * @param string $organizationId The name of a new folder to be created and associated to the document. If you do not wish to create a new folder you may set this as null.
     *
     * @return $this
     */
    public function setOrganizationId($organizationId)
    {
        $this->container['organizationId'] = $organizationId;

        return $this;
    }

    /**
     * Gets type
     *
     * @return string
     */
    public function getType()
    {
        return $this->container['type'];
    }

    /**
     * Sets type
     *
     * @param string $type The name of a new folder to be created and associated to the document. If you do not wish to create a new folder you may set this as null.
     *
     * @return $this
     */
    public function setType($type)
    {
        $this->container['type'] = $type;

        return $this;
    }

    /**
     * Gets notaryType
     *
     * @return string
     */
    public function getNotaryType()
    {
        return $this->container['notaryType'];
    }

    /**
     * Sets notaryType
     *
     * @param string $notaryType The name of a new folder to be created and associated to the document. If you do not wish to create a new folder you may set this as null.
     *
     * @return $this
     */
    public function setNotaryType($notaryType)
    {
        $this->container['notaryType'] = $notaryType;

        return $this;
    }
    
    /**
     * Gets expirationDate
     *
     * @return \DateTime
     */
    public function getExpirationDate()
    {
        return $this->container['expirationDate'];
    }

    /**
     * Sets expirationDate
     *
     * @param \DateTime $expirationDate The expiration date of the document. Any time information will be discarded, as the expiration will be set   to the last time available for the chosen date in the default timezone.
     *
     * @return $this
     */
    public function setExpirationDate($expirationDate)
    {
        $this->container['expirationDate'] = $expirationDate;

        return $this;
    }

        /**
     * Gets notifiedEmails
     *
     * @return string[]
     */
    public function getNotifiedEmails()
    {
        return $this->container['notifiedEmails'];
    }

    /**
     * Sets notifiedEmails
     *
     * @param string[] $notifiedEmails The emails to notify when the document is concluded.
     *
     * @return $this
     */
    public function setNotifiedEmails($notifiedEmails)
    {
        $this->container['notifiedEmails'] = $notifiedEmails;

        return $this;
    }

    /**
     * Gets files
     *
     * @return \Lacuna\Signer\Model\FileUploadModel[]
     */
    public function getDataFile()
    {
        return $this->container['dataFile'];
    }
    
    /**
     * Sets files
     *
     * @param \Lacuna\Signer\Model\FileUploadModel[] $files The files to submit. Each file will create a document.
     *
     * @return $this
     */
    public function setDataFile($dataFile)
    {
        $this->container['dataFile'] = $dataFile;
        
        return $this;
    }

    /**
     * Gets files
     *
     * @return \Lacuna\Signer\Model\FileUploadModel[]
     */
    public function getTemplateFile()
    {
        return $this->container['templateFile'];
    }
    
    /**
     * Sets files
     *
     * @param \Lacuna\Signer\Model\FileUploadModel[] $files The files to submit. Each file will create a document.
     *
     * @return $this
     */
    public function setTemplateFile($templateFile)
    {
        $this->container['templateFile'] = $templateFile;
        
        return $this;
    }

    /**
     * Returns true if offset exists. False otherwise.
     *
     * @param integer $offset Offset
     *
     * @return boolean
     */
    public function offsetExists($offset)
    {
        return isset($this->container[$offset]);
    }

    /**
     * Gets offset.
     *
     * @param integer $offset Offset
     *
     * @return mixed
     */
    public function offsetGet($offset)
    {
        return isset($this->container[$offset]) ? $this->container[$offset] : null;
    }

    /**
     * Sets value based on offset.
     *
     * @param integer $offset Offset
     * @param mixed   $value  Value to be set
     *
     * @return void
     */
    public function offsetSet($offset, $value)
    {
        if (is_null($offset)) {
            $this->container[] = $value;
        } else {
            $this->container[$offset] = $value;
        }
    }

    /**
     * Unsets offset.
     *
     * @param integer $offset Offset
     *
     * @return void
     */
    public function offsetUnset($offset)
    {
        unset($this->container[$offset]);
    }

    /**
     * Gets the string presentation of the object
     *
     * @return string
     */
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
