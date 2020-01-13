package com.lacunasoftware.signer.client;


public class SignatureEvidencesModel {
	private GeolocationModel geolocation;

	SignatureEvidencesModel(LacunaSignerApiSignatureSignatureEvidencesModel model) {
		if (model.getGeolocation() != null) {
			this.geolocation = new GeolocationModel(model.getGeolocation());
		}
	}

	public GeolocationModel getGeolocation() {
		return geolocation;
	}

	public void setGeolocation(GeolocationModel geolocation) {
		this.geolocation = geolocation;
	}

	LacunaSignerApiSignatureSignatureEvidencesModel toModel() {
		LacunaSignerApiSignatureSignatureEvidencesModel model = new LacunaSignerApiSignatureSignatureEvidencesModel();
		if (geolocation != null) {
			model.setGeolocation(geolocation.toModel());
		}
		return model;
	}
}
