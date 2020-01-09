package com.lacunasoftware.signer.client;


public class ObserverCreateModel {
	private ParticipantUserModel user;

	public ParticipantUserModel getUser() {
		return user;
	}

	public void setUser(ParticipantUserModel user) {
		this.user = user;
	}

	LacunaSignerApiObserversObserverCreateModel toModel() {
		LacunaSignerApiObserversObserverCreateModel model = new LacunaSignerApiObserversObserverCreateModel();
		if (user != null) {
			model.setUser(user.toModel());
		}
		return model;
	}
}
