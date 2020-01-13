package com.lacunasoftware.signer.client;


import com.google.gson.annotations.SerializedName;


public class LacunaSignerApiSignatureGeolocationModel {

	@SerializedName("latitude")
	private Double latitude;

	@SerializedName("longitude")
	private Double longitude;

	@SerializedName("accuracy")
	private Double accuracy;

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(Double accuracy) {
		this.accuracy = accuracy;
	}
}
