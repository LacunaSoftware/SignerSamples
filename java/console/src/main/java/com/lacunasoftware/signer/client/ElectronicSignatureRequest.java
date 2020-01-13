package com.lacunasoftware.signer.client;


import java.util.UUID;


public class ElectronicSignatureRequest {
	private UUID flowActionId;
	private PdfMarkPosition signaturePosition;
	private String authenticationCode;
	private AuthenticationTypes authenticationType;
	private SignatureEvidencesModel evidences;

	ElectronicSignatureRequest(LacunaSignerApiSignatureElectronicSignatureRequest model) {
		this.flowActionId = model.getFlowActionId();
		this.authenticationCode = model.getAuthenticationCode();

		if (model.getSignaturePosition() != null) {
			this.signaturePosition = new PdfMarkPosition(model.getSignaturePosition());
		}

		if (model.getAuthenticationType() != null) {
			this.authenticationType = AuthenticationTypes.valueOf(model.getAuthenticationType().name());
		}

		if (model.getEvidences() != null) {
			this.evidences = new SignatureEvidencesModel(model.getEvidences());
		}
	}

	public UUID getFlowActionId() {
		return flowActionId;
	}

	public void setFlowActionId(UUID flowActionId) {
		this.flowActionId = flowActionId;
	}

	public PdfMarkPosition getSignaturePosition() {
		return signaturePosition;
	}

	public void setSignaturePosition(PdfMarkPosition signaturePosition) {
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

	public SignatureEvidencesModel getEvidences() {
		return evidences;
	}

	public void setEvidences(SignatureEvidencesModel evidences) {
		this.evidences = evidences;
	}

	LacunaSignerApiSignatureElectronicSignatureRequest toModel() {
		LacunaSignerApiSignatureElectronicSignatureRequest model = new LacunaSignerApiSignatureElectronicSignatureRequest();
		model.setFlowActionId(flowActionId);
		model.setAuthenticationCode(authenticationCode);

		if (signaturePosition != null) {
			model.setSignaturePosition(signaturePosition.toModel());
		}

		if (authenticationType != null) {
			model.setAuthenticationType(LacunaSignerApiSignatureElectronicSignatureRequest.AuthenticationTypes.valueOf(authenticationType.name()));
		}

		if (evidences != null) {
			model.setEvidences(evidences.toModel());
		}

		return model;
	}
}
