package com.lacunasoftware.signer.client;

/**
 * Thrown to indicate that a REST request returned an HTTP status code indicating error and possibly
 * a message, but no further information was returned.
 */
public class RestErrorException extends RestException {
	private int statusCode;
	private String errorMessage;

	RestErrorException(String verb, String url, int statusCode) {
		this(verb, url, statusCode, null);
	}

	RestErrorException(String verb, String url, int statusCode, String errorMessage) {
		super(formatExceptionMessage(verb, url, statusCode, errorMessage), verb, url);
		this.statusCode = statusCode;
		this.errorMessage = errorMessage;
	}

	private static String formatExceptionMessage(String verb, String url, int statusCode, String errorMessage) {
		if (errorMessage != null) {
			return String.format("REST action %s %s returned HTTP error %d: %s", verb, url, statusCode, errorMessage);
		} else {
			return String.format("REST action %s %s returned HTTP error %d", verb, url, statusCode);
		}
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}