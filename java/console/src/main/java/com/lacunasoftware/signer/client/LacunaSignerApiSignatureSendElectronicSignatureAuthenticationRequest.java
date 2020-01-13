package com.lacunasoftware.signer.client;


import com.google.gson.annotations.SerializedName;

import java.util.UUID;


public class LacunaSignerApiSignatureSendElectronicSignatureAuthenticationRequest {

	@SerializedName("flowActionId")
	private UUID flowActionId;

	@SerializedName("resend")
	private boolean resend;

	public UUID getFlowActionId() {
		return flowActionId;
	}

	public void setFlowActionId(UUID flowActionId) {
		this.flowActionId = flowActionId;
	}

	public boolean isResend() {
		return resend;
	}

	public void setResend(boolean resend) {
		this.resend = resend;
	}
}
