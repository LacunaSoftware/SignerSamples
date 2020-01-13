package com.lacunasoftware.signer.client;


import java.util.UUID;


public class SendElectronicSignatureAuthenticationRequest {
	private UUID flowActionId;
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

	LacunaSignerApiSignatureSendElectronicSignatureAuthenticationRequest toModel() {
		LacunaSignerApiSignatureSendElectronicSignatureAuthenticationRequest model = new LacunaSignerApiSignatureSendElectronicSignatureAuthenticationRequest();
		model.setFlowActionId(flowActionId);
		model.setResend(resend);
		return model;
	}
}
