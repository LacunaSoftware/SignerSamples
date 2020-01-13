package com.lacunasoftware.signer.client;


import com.google.gson.annotations.SerializedName;


public class LacunaSignerApiSignatureStartSignatureResponse {

	@SerializedName("success")
	private boolean success;

	@SerializedName("token")
	private String token;

	@SerializedName("toSignHash")
	private byte[] toSignHash;

	@SerializedName("digestAlgorithm")
	private String digestAlgorithm;

	@SerializedName("validationResults")
	public LacunaSignerApiValidationResultsModel validationResults;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public byte[] getToSignHash() {
		return toSignHash;
	}

	public void setToSignHash(byte[] toSignHash) {
		this.toSignHash = toSignHash;
	}

	public String getDigestAlgorithm() {
		return digestAlgorithm;
	}

	public void setDigestAlgorithm(String digestAlgorithm) {
		this.digestAlgorithm = digestAlgorithm;
	}

	public LacunaSignerApiValidationResultsModel getValidationResults() {
		return validationResults;
	}

	public void setValidationResults(LacunaSignerApiValidationResultsModel validationResults) {
		this.validationResults = validationResults;
	}
}
