package com.lacunasoftware.signer.client;


import java.util.UUID;


public class StartSignatureRequest {
	private UUID flowActionId;
	private byte[] certificate;
	private PdfMarkPosition signaturePosition;

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

	public PdfMarkPosition getSignaturePosition() {
		return signaturePosition;
	}

	public void setSignaturePosition(PdfMarkPosition signaturePosition) {
		this.signaturePosition = signaturePosition;
	}

	LacunaSignerApiSignatureStartSignatureRequest toModel() {
		LacunaSignerApiSignatureStartSignatureRequest model = new LacunaSignerApiSignatureStartSignatureRequest();
		model.setFlowActionId(flowActionId);
		model.setCertificate(certificate);

		if (signaturePosition != null) {
			model.setSignaturePosition(signaturePosition.toModel());
		}

		return model;
	}
}
