package com.lacunasoftware.signer.client;


import com.google.gson.annotations.SerializedName;


public class LacunaSignerApiSignatureCompleteSignatureRequest {

	@SerializedName("signature")
	private byte[] signature;

	@SerializedName("token")
	private String token;

	public byte[] getSignature() {
		return signature;
	}

	public void setSignature(byte[] signature) {
		this.signature = signature;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
