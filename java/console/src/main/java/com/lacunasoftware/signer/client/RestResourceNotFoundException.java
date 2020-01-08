package com.lacunasoftware.signer.client;

/**
 * Thrown to indicate that a REST request was made but not resource was found at the requested URL.
 */
public class RestResourceNotFoundException extends RestException {
	private String resourceName;
	private String resourceId;

	RestResourceNotFoundException(String verb, String url, String resourceName, String resourceId) {
		super(formatExceptionMessage(verb, url, resourceName, resourceId), verb, url);
		this.resourceName = resourceName;
		this.resourceId = resourceId;
	}

	private static String formatExceptionMessage(String verb, String url, String resourceName, String resourceId) {
		return String.format("REST action %s %s returned resource not found: %s %s", verb, url, resourceName, resourceId);
	}

	/**
	 * Returns the name of the resource type that was not found (for instance, "SecurityContext").
	 * @return Name of the resource type that was not found.
	 */
	public String getResourceName() {
		return resourceName;
	}

	/**
	 * Returns the identifier of the resource that was not found.
	 * @return The identifier of the resource that was not found.
	 */
	public String getResourceId() {
		return resourceId;
	}
}
