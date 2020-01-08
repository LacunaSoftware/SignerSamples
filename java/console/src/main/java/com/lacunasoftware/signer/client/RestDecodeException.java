package com.lacunasoftware.signer.client;

/**
 * Thrown to indicate that a REST request was successful but an error occurred while decoding its
 * response.
 */
public class RestDecodeException extends RestException {

	RestDecodeException(String verb, String url, Exception innerException) {
		super(formatExceptionMessage(verb, url), verb, url, innerException);
	}

	private static String formatExceptionMessage(String verb, String url) {
		return String.format("REST action %s %s returned success but an error occurred decoding the response (see inner exception for details)", verb, url);
	}
}

