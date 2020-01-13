package com.lacunasoftware.signer.client;


public class StartSignatureResponse {
	private boolean success;
	private String token;
	private byte[] toSignHash;
	private String digestAlgorithm;
	private ValidationResults validationResults;

	StartSignatureResponse(LacunaSignerApiSignatureStartSignatureResponse model) {
		this.success = model.isSuccess();
		this.token = model.getToken();
		this.toSignHash = model.getToSignHash();
		this.digestAlgorithm = model.getDigestAlgorithm();

		if (model.getValidationResults() != null) {
			this.validationResults = new ValidationResults(model.getValidationResults());
		}
	}

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

	public ValidationResults getValidationResults() {
		return validationResults;
	}

	public void setValidationResults(ValidationResults validationResults) {
		this.validationResults = validationResults;
	}
}
