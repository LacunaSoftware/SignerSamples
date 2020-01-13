package com.lacunasoftware.signer.client;


public class CompleteSignatureRequest {
	private byte[] signature;
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

	LacunaSignerApiSignatureCompleteSignatureRequest toModel() {
		LacunaSignerApiSignatureCompleteSignatureRequest model = new LacunaSignerApiSignatureCompleteSignatureRequest();
		model.setSignature(signature);
		model.setToken(token);
		return model;
	}
}
