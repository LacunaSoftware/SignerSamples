package com.lacunasoftware.signer.client;


public class CompleteSignatureResponse {
	private boolean success;
	private ValidationResults validationResults;

	CompleteSignatureResponse(LacunaSignerApiSignatureCompleteSignatureResponse model) {
		this.success = model.isSuccess();

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

	public ValidationResults getValidationResults() {
		return validationResults;
	}

	public void setValidationResults(ValidationResults validationResults) {
		this.validationResults = validationResults;
	}
}
