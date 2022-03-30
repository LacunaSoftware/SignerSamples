<?php

namespace Lacuna\Scenarios\DocumentGeneration;

use Lacuna\Signer\PhpClient\Models\UploadModel;
use Lacuna\Signer\Model\FileUploadModel;

class DocumentGenerationModel extends UploadModel
{   
  
  public $percentDone;
  public $displayName;
  public $size;

  public function __construct($model){
        parent::__construct($model);
  }

  public function getPercentDone(){

    return $this->percentDone;
  }
  
  public function getSize(){

    return $this->size;
  }
  public function setPercentDone($percentDone){
  
  $this->percentDone = $percentDone;
  }
 
  public function setSize($size){
    
      $this->size = $size;
  }
  

  public function getDisplayName()
  {
      return $this->displayName;
  }

  public function setDisplayName($displayName)
  {
      $this->displayName = $displayName;
  }

  function toModel()
  {
      $model = new DocumentGenerationRequest();
      $model->setId($this->id);
      $model->setName($this->name);
      $model->setContentType($this->contentType);
      $model->setDisplayName($this->displayName);
      $model->setPercentDone($this->percentDone);
      $model->setSize($this->size);
      return $model;
  }
    

}