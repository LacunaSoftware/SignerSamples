package com.lacunasoftware.signer.client;


import com.google.gson.annotations.SerializedName;

import java.util.UUID;


public class LacunaSignerApiUsersUserDisplayModel {

	@SerializedName("id")
	private UUID id;

	@SerializedName("name")
	private String name;

	@SerializedName("emailAddress")
	private String emailAddress;

	@SerializedName("identifier")
	private String identifier;

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

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
}
