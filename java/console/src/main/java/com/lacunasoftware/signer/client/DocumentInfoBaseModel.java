package com.lacunasoftware.signer.client;


import java.util.Date;
import java.util.UUID;


public class DocumentInfoBaseModel {
	private UUID id;
	private String name;
	private String filename;
	private long fileSize;
	private String mimeType;
	private boolean hasSignature;
	private boolean isConcluded;
	private FolderInfoModel folder;
	private OrganizationInfoModel organization;
	private Date creationDate;
	private Date updateDate;
	private CreatorModel createdBy;

	DocumentInfoBaseModel(LacunaSignerApiDocumentsDocumentModel model) {
		this.id = model.getId();
		this.name = model.getName();
		this.filename = model.getFilename();
		this.fileSize = model.getFileSize();
		this.mimeType = model.getMimeType();
		this.hasSignature = model.isHasSignature();
		this.isConcluded = model.isIsConcluded();

		if (model.getFolder() != null) {
			this.folder = new FolderInfoModel(model.getFolder());
		}
		if (model.getOrganization() != null) {
			this.organization = new OrganizationInfoModel(model.getOrganization());
		}
		if (model.getCreationDate() != null) {
			this.creationDate = model.getCreationDate();
		}
		if (model.getUpdateDate() != null) {
			this.updateDate = model.getUpdateDate();
		}
		if (model.getCreatedBy() != null) {
			this.createdBy = new CreatorModel(model.getCreatedBy());
		}
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public boolean isHasSignature() {
		return hasSignature;
	}

	public void setHasSignature(boolean hasSignature) {
		this.hasSignature = hasSignature;
	}

	public boolean isConcluded() {
		return isConcluded;
	}

	public void setConcluded(boolean concluded) {
		isConcluded = concluded;
	}

	public FolderInfoModel getFolder() {
		return folder;
	}

	public void setFolder(FolderInfoModel folder) {
		this.folder = folder;
	}

	public OrganizationInfoModel getOrganization() {
		return organization;
	}

	public void setOrganization(OrganizationInfoModel organization) {
		this.organization = organization;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public CreatorModel getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(CreatorModel createdBy) {
		this.createdBy = createdBy;
	}
}
