package com.lacunasoftware.signer.client;


import com.google.gson.annotations.SerializedName;


public class LacunaSignerApiSignatureSignatureEvidencesModel {

	@SerializedName("geolocation")
	private LacunaSignerApiSignatureGeolocationModel geolocation;

	public LacunaSignerApiSignatureGeolocationModel getGeolocation() {
		return geolocation;
	}

	public void setGeolocation(LacunaSignerApiSignatureGeolocationModel geolocation) {
		this.geolocation = geolocation;
	}
}
