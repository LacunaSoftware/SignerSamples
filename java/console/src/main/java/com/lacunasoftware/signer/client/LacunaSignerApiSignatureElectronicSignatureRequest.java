package com.lacunasoftware.signer.client;


import com.google.gson.annotations.SerializedName;

import java.util.UUID;


public class LacunaSignerApiSignatureElectronicSignatureRequest {
	public enum AuthenticationTypes {
		SMS("SMS"),
		AUTHENTICATION_APP("AuthenticationApp");

		private String value;

		AuthenticationTypes(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	@SerializedName("flowActionId")
	private UUID flowActionId;

	@SerializedName("signaturePosition")
	private LacunaSignerApiSignaturePdfMarkPosition signaturePosition;

	@SerializedName("authenticationCode")
	private String authenticationCode;

	@SerializedName("authenticationType")
	private AuthenticationTypes authenticationType;

	@SerializedName("evidences")
	private LacunaSignerApiSignatureSignatureEvidencesModel evidences;

	public UUID getFlowActionId() {
		return flowActionId;
	}

	public void setFlowActionId(UUID flowActionId) {
		this.flowActionId = flowActionId;
	}

	public LacunaSignerApiSignaturePdfMarkPosition getSignaturePosition() {
		return signaturePosition;
	}

	public void setSignaturePosition(LacunaSignerApiSignaturePdfMarkPosition signaturePosition) {
		this.signaturePosition = signaturePosition;
	}

	public String getAuthenticationCode() {
		return authenticationCode;
	}

	public void setAuthenticationCode(String authenticationCode) {
		this.authenticationCode = authenticationCode;
	}

	public AuthenticationTypes getAuthenticationType() {
		return authenticationType;
	}

	public void setAuthenticationType(AuthenticationTypes authenticationType) {
		this.authenticationType = authenticationType;
	}

	public LacunaSignerApiSignatureSignatureEvidencesModel getEvidences() {
		return evidences;
	}

	public void setEvidences(LacunaSignerApiSignatureSignatureEvidencesModel evidences) {
		this.evidences = evidences;
	}
}
