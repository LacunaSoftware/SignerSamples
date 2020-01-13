package com.lacunasoftware.signer.client;


import com.google.gson.annotations.SerializedName;

import java.util.List;


public class LacunaSignerApiValidationResultsModel {

	@SerializedName("passedChecks")
	private List<LacunaSignerApiValidationItemModel> passedChecks;

	@SerializedName("errors")
	private List<LacunaSignerApiValidationItemModel> errors;

	@SerializedName("warnings")
	private List<LacunaSignerApiValidationItemModel> warnings;

	@SerializedName("isValid")
	private boolean isValid;

	public List<LacunaSignerApiValidationItemModel> getPassedChecks() {
		return passedChecks;
	}

	public void setPassedChecks(List<LacunaSignerApiValidationItemModel> passedChecks) {
		this.passedChecks = passedChecks;
	}

	public List<LacunaSignerApiValidationItemModel> getErrors() {
		return errors;
	}

	public void setErrors(List<LacunaSignerApiValidationItemModel> errors) {
		this.errors = errors;
	}

	public List<LacunaSignerApiValidationItemModel> getWarnings() {
		return warnings;
	}

	public void setWarnings(List<LacunaSignerApiValidationItemModel> warnings) {
		this.warnings = warnings;
	}

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean valid) {
		isValid = valid;
	}
}
