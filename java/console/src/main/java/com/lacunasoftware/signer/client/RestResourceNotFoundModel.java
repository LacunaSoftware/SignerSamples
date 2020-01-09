package com.lacunasoftware.signer.client;

import com.google.gson.annotations.SerializedName;


class RestResourceNotFoundModel extends RestGeneralErrorModel {

	@SerializedName("resourceName")
	private String resourceName = null;

	@SerializedName("resourceId")
	private String resourceId = null;

	String getResourceName() {
		return resourceName;
	}
	void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	String getResourceId() {
		return resourceId;
	}
	void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
}
