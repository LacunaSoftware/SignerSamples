package com.lacunasoftware.signer.client;


import java.util.Date;
import java.util.UUID;


public class SignRuleUserModel {
	private UUID id;
	private String name;
	private String identifier;
	private String email;
	private Date signatureDate;

	SignRuleUserModel(LacunaSignerApiFlowActionsSignRuleUserModel model) {
		this.id = model.getId();
		this.name = model.getName();
		this.identifier = model.getIdentifier();
		this.email = model.getEmail();

		if (model.getSignatureDate() != null) {
			this.signatureDate = model.getSignatureDate();
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

	public Date getSignatureDate() {
		return signatureDate;
	}

	public void setSignatureDate(Date signatureDate) {
		this.signatureDate = signatureDate;
	}
}
