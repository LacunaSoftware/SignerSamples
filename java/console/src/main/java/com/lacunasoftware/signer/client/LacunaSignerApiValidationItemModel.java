package com.lacunasoftware.signer.client;


import com.google.gson.annotations.SerializedName;


public class LacunaSignerApiValidationItemModel {

	@SerializedName("type")
	private String type;

	@SerializedName("message")
	private String message;

	@SerializedName("detail")
	private String detail;

	@SerializedName("innerValidationResults")
	private LacunaSignerApiValidationResultsModel innerValidationResults;

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

	public LacunaSignerApiValidationResultsModel getInnerValidationResults() {
		return innerValidationResults;
	}

	public void setInnerValidationResults(LacunaSignerApiValidationResultsModel innerValidationResults) {
		this.innerValidationResults = innerValidationResults;
	}
}
