package com.lacunasoftware.signer.client;


public class FileUploadModel extends UploadModel {
	private String displayName;

	public FileUploadModel() {

	}

	public FileUploadModel(UploadModel uploadModel) {
		this.id = uploadModel.getId();
		this.name = uploadModel.getName();
		this.contentType = uploadModel.getContentType();
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	LacunaSignerApiFileUploadModel toModel() {
		LacunaSignerApiFileUploadModel model = new LacunaSignerApiFileUploadModel();
		model.setId(id);
		model.setName(name);
		model.setContentType(contentType);
		model.setDisplayName(displayName);
		return model;
	}
}
