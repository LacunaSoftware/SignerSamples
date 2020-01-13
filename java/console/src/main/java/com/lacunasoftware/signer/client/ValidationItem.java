package com.lacunasoftware.signer.client;


public class ValidationItem {
	private String type;
	private String message;
	private String detail;
	private ValidationResults innerValidationResults;

	ValidationItem(LacunaSignerApiValidationItemModel model) {
		this.type = model.getType();
		this.message = model.getMessage();
		this.detail = model.getDetail();

		if (model.getInnerValidationResults() != null) {
			this.innerValidationResults = new ValidationResults(model.getInnerValidationResults());
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public ValidationResults getInnerValidationResults() {
		return innerValidationResults;
	}

	public void setInnerValidationResults(ValidationResults innerValidationResults) {
		this.innerValidationResults = innerValidationResults;
	}

	@Override
	public String toString() {
		return toString(0);
	}

	String toString(int identationLevel) {
		StringBuilder text = new StringBuilder();
		text.append(message);
		if (detail != null) {
			text.append(String.format(" (%s)", detail));
		}
		if (innerValidationResults != null) {
			text.append('\n');
			text.append(innerValidationResults.toString(identationLevel + 1));
		}
		return text.toString();
	}
}
