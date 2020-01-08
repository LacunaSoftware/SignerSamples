package com.lacunasoftware.signer.client;


import java.util.UUID;


public class ParticipantUserModel {
	private UUID id;
	private String name;
	private String identifier;
	private String email;

	public ParticipantUserModel() {
	}

	ParticipantUserModel(LacunaSignerApiUsersParticipantUserModel model) {
		this.id = model.getId();
		this.name = model.getName();
		this.identifier = model.getIdentifier();
		this.email = model.getEmail();
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	LacunaSignerApiUsersParticipantUserModel toModel() {
		LacunaSignerApiUsersParticipantUserModel model = new LacunaSignerApiUsersParticipantUserModel();
		model.setId(id);
		model.setName(name);
		model.setIdentifier(identifier);
		model.setEmail(email);
		return model;
	}
}
