package com.lacunasoftware.signer.client;


import java.util.UUID;


public class OrganizationInfoModel {
	private UUID id;
	private String name;
	private String identifier;

	OrganizationInfoModel(LacunaSignerApiOrganizationsOrganizationInfoModel model) {
		this.id = model.getId();
		this.name = model.getName();
		this.identifier = model.getIdentifier();
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

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
}
