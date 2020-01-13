package com.lacunasoftware.signer.client;


import java.util.UUID;


public class FolderCreateRequest {
	private String name;
	private UUID organizationId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UUID getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(UUID organizationId) {
		this.organizationId = organizationId;
	}

	LacunaSignerApiFoldersFolderCreateRequest toModel() {
		LacunaSignerApiFoldersFolderCreateRequest model = new LacunaSignerApiFoldersFolderCreateRequest();
		model.setName(name);
		model.setOrganizationId(organizationId);
		return model;
	}
}
