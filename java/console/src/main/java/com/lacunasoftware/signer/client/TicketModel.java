package com.lacunasoftware.signer.client;


public class TicketModel {
	private String location;

	TicketModel(LacunaSignerApiTicketModel model) {
		this.location = model.getLocation();
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}
