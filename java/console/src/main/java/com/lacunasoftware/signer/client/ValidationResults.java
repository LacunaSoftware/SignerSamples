package com.lacunasoftware.signer.client;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ValidationResults {
	private List<ValidationItem> passedChecks = new ArrayList<ValidationItem>();
	private List<ValidationItem> errors = new ArrayList<ValidationItem>();
	private List<ValidationItem> warnings = new ArrayList<ValidationItem>();

	ValidationResults(LacunaSignerApiValidationResultsModel model) {
		convertItems(model.getPassedChecks(), this.passedChecks);
		convertItems(model.getWarnings(), this.warnings);
		convertItems(model.getErrors(), this.errors);
	}

	public List<ValidationItem> getPassedChecks() {
		return passedChecks;
	}

	public void setPassedChecks(List<ValidationItem> passedChecks) {
		this.passedChecks = passedChecks;
	}

	public List<ValidationItem> getErrors() {
		return errors;
	}

	public void setErrors(List<ValidationItem> errors) {
		this.errors = errors;
	}

	public List<ValidationItem> getWarnings() {
		return warnings;
	}

	public void setWarnings(List<ValidationItem> warnings) {
		this.warnings = warnings;
	}

	public int getChecksPerformed() {
		return passedChecks.size() + warnings.size() + errors.size();
	}

	public boolean isValid() {
		return errors.isEmpty();
	}

	public boolean hasErrors() {
		return !errors.isEmpty();
	}

	public boolean hasWarnings() {
		return !warnings.isEmpty();
	}

	public String getSummary() {
		return getSummary(0);
	}

	@Override
	public String toString() {
		return toString(0);
	}

	String toString(int identationLevel) {
		String tab = Util.repeatChar('\t', identationLevel);
		StringBuilder text = new StringBuilder();
		text.append(getSummary(identationLevel));
		if (hasErrors()) {
			text.append('\n');
			text.append(tab);
			text.append("Errors:");
			text.append('\n');
			appendItems(text, errors, identationLevel);
		}
		if (hasWarnings()) {
			text.append('\n');
			text.append(tab);
			text.append("Warnings:");
			text.append('\n');
			appendItems(text, warnings, identationLevel);
		}
		if (!passedChecks.isEmpty()) {
			text.append('\n');
			text.append(tab);
			text.append("Passed checks:");
			text.append('\n');
			appendItems(text, passedChecks, identationLevel);
		}
		return text.toString();
	}

	private String getSummary(int identationLevel) {
		String tab = Util.repeatChar('\t', identationLevel);
		StringBuilder text = new StringBuilder();
		text.append(tab);
		text.append("Validation results: ");
		if (getChecksPerformed() == 0) {
			text.append("no checks performed");
		} else {
			text.append(String.format("%d checks performed", getChecksPerformed()));
			if (hasErrors()) {
				text.append(String.format(", %d errors", errors.size()));
			}
			if (hasWarnings()) {
				text.append(String.format(", %d warnings", warnings.size()));
			}
			if (!passedChecks.isEmpty()) {
				if (!hasErrors() && !hasWarnings()) {
					text.append(", all passed");
				} else {
					text.append(String.format(", %d passed", passedChecks.size()));
				}
			}
		}
		return text.toString();
	}

	private static void convertItems(List<LacunaSignerApiValidationItemModel> from, List<ValidationItem> to) {
		for (LacunaSignerApiValidationItemModel model : from) {
			to.add(new ValidationItem(model));
		}
	}

	private static void appendItems(StringBuilder text, List<ValidationItem> items, int identationLevel) {
		boolean isFirst = true;
		String tab = Util.repeatChar('\t', identationLevel);
		for (ValidationItem item : items) {
			if (isFirst) {
				isFirst = false;
			} else {
				text.append('\n');
			}
			text.append(tab);
			text.append("- ");
			text.append(item.toString(identationLevel));
		}
	}


}
