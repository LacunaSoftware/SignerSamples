package com.lacunasoftware.signer.client;


public class GeolocationModel {
	private double latitude;
	private double longitude;
	private double accuracy;

	GeolocationModel(LacunaSignerApiSignatureGeolocationModel model) {
		this.latitude = model.getLatitude();
		this.longitude = model.getLongitude();
		this.accuracy = model.getAccuracy();
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}

	LacunaSignerApiSignatureGeolocationModel toModel() {
		LacunaSignerApiSignatureGeolocationModel model = new LacunaSignerApiSignatureGeolocationModel();
		model.setLatitude(latitude);
		model.setLongitude(longitude);
		model.setAccuracy(accuracy);
		return model;
	}
}
