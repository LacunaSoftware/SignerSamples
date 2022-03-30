<?php

namespace Lacuna\Scenarios\DocumentGeneration;

class GenerationDocumentResultModel
{   
  
  public $folderId;
  public $subscriptionId;
  public $id;
  public $type;
  public $agentId;
  public $totalDocumentsCount;
  public $initializedCount;
  public $completedCount;
  public $status;



  function toModel()
  {
      // $model = new UploadGenerateBuilder();
      // $model->setId($this->id);
      // $model->setName($this->name);
      // $model->setContentType($this->contentType);
      // $model->setDisplayName($this->displayName);
      // $model->setPercentDone($this->percentDone);
      // $model->setSize($this->size);
      // return $model;
  }
  
  public function __construct($model){

  }


  /**
   * Get the value of status
   */ 
  public function getStatus()
  {
    return $this->status;
  }

  /**
   * Set the value of status
   *
   * @return  self
   */ 
  public function setStatus($status)
  {
    $this->status = $status;

    return $this;
  }

  /**
   * Get the value of completedCount
   */ 
  public function getCompletedCount()
  {
    return $this->completedCount;
  }

  /**
   * Set the value of completedCount
   *
   * @return  self
   */ 
  public function setCompletedCount($completedCount)
  {
    $this->completedCount = $completedCount;

    return $this;
  }

  /**
   * Get the value of initializedCount
   */ 
  public function getInitializedCount()
  {
    return $this->initializedCount;
  }

  /**
   * Set the value of initializedCount
   *
   * @return  self
   */ 
  public function setInitializedCount($initializedCount)
  {
    $this->initializedCount = $initializedCount;

    return $this;
  }

  /**
   * Get the value of totalDocumentsCount
   */ 
  public function getTotalDocumentsCount()
  {
    return $this->totalDocumentsCount;
  }

  /**
   * Set the value of totalDocumentsCount
   *
   * @return  self
   */ 
  public function setTotalDocumentsCount($totalDocumentsCount)
  {
    $this->totalDocumentsCount = $totalDocumentsCount;

    return $this;
  }

  /**
   * Get the value of agentId
   */ 
  public function getAgentId()
  {
    return $this->agentId;
  }

  /**
   * Set the value of agentId
   *
   * @return  self
   */ 
  public function setAgentId($agentId)
  {
    $this->agentId = $agentId;

    return $this;
  }

  /**
   * Get the value of type
   */ 
  public function getType()
  {
    return $this->type;
  }

  /**
   * Set the value of type
   *
   * @return  self
   */ 
  public function setType($type)
  {
    $this->type = $type;

    return $this;
  }

  /**
   * Get the value of folderId
   */ 
  public function getFolderId()
  {
    return $this->folderId;
  }

  /**
   * Set the value of folderId
   *
   * @return  self
   */ 
  public function setFolderId($folderId)
  {
    $this->folderId = $folderId;

    return $this;
  }

  /**
   * Get the value of subscriptionId
   */ 
  public function getSubscriptionId()
  {
    return $this->subscriptionId;
  }

  /**
   * Set the value of subscriptionId
   *
   * @return  self
   */ 
  public function setSubscriptionId($subscriptionId)
  {
    $this->subscriptionId = $subscriptionId;

    return $this;
  }

  /**
   * Get the value of id
   */ 
  public function getId()
  {
    return $this->id;
  }

  /**
   * Set the value of id
   *
   * @return  self
   */ 
  public function setId($id)
  {
    $this->id = $id;

    return $this;
  }
}