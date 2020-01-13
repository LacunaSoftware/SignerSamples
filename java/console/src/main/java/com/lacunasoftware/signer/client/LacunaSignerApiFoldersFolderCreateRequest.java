package com.lacunasoftware.signer.client;


import com.google.gson.annotations.SerializedName;

import java.util.UUID;


public class LacunaSignerApiFoldersFolderCreateRequest {

	@SerializedName("name")
	private String name;

	@SerializedName("organizationId")
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
}
