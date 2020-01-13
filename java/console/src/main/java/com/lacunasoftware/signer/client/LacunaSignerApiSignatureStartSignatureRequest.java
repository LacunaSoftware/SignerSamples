package com.lacunasoftware.signer.client;


import com.google.gson.annotations.SerializedName;

import java.util.UUID;


public class LacunaSignerApiSignatureStartSignatureRequest {

	@SerializedName("flowActionId")
	private UUID flowActionId;

	@SerializedName("certificate")
	private byte[] certificate;

	@SerializedName("signaturePosition")
	private LacunaSignerApiSignaturePdfMarkPosition signaturePosition;

	public UUID getFlowActionId() {
		return flowActionId;
	}

	public void setFlowActionId(UUID flowActionId) {
		this.flowActionId = flowActionId;
	}

	public byte[] getCertificate() {
		return certificate;
	}

	public void setCertificate(byte[] certificate) {
		this.certificate = certificate;
	}

	public LacunaSignerApiSignaturePdfMarkPosition getSignaturePosition() {
		return signaturePosition;
	}

	public void setSignaturePosition(LacunaSignerApiSignaturePdfMarkPosition signaturePosition) {
		this.signaturePosition = signaturePosition;
	}
}
