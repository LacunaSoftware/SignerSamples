package com.lacunasoftware.signer.client;


public enum DocumentTicketType {
	ORIGINAL("Original"),
	PRINTER_FRIENDLY_VERSION("PrinterFriendlyVersion"),
	SIGNATURES("Signatures"),
	ORIGINAL_WITH_MARKS("OriginalWithMarks");

	private String value;

	DocumentTicketType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
