package com.lacunasoftware.signer.client;


import java.util.UUID;


public class ObserverModel {
	private UUID id;
	private ParticipantUserModel user;

	ObserverModel(LacunaSignerApiObserversObserverModel model) {
		this.id = model.getId();

		if (model.getUser() != null) {
			this.user = new ParticipantUserModel(model.getUser());
		}
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public ParticipantUserModel getUser() {
		return user;
	}

	public void setUser(ParticipantUserModel user) {
		this.user = user;
	}
}
