package com.lacunasoftware.signer.client;

import com.google.gson.annotations.SerializedName;


class RestGeneralErrorModel {

	@SerializedName("message")
	private String message = null;

	String getMessage() {
		return message;
	}
	void setMessage(String message) {
		this.message = message;
	}
}
