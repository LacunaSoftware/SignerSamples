package com.lacunasoftware.signer.client;


import com.google.gson.annotations.SerializedName;


public class LacunaSignerApiSignatureCompleteSignatureResponse {

	@SerializedName("success")
	private boolean success;

	@SerializedName("validationResults")
	private LacunaSignerApiValidationResultsModel validationResults;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public LacunaSignerApiValidationResultsModel getValidationResults() {
		return validationResults;
	}

	public void setValidationResults(LacunaSignerApiValidationResultsModel validationResults) {
		this.validationResults = validationResults;
	}
}
