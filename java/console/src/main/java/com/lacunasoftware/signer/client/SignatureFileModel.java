package com.lacunasoftware.signer.client;


public class SignatureFileModel extends FileUploadModel {
	private PdfMarkPosition signaturePosition;
	private PdfMarkPosition authenticationStampPosition;

	public PdfMarkPosition getSignaturePosition() {
		return signaturePosition;
	}

	public void setSignaturePosition(PdfMarkPosition signaturePosition) {
		this.signaturePosition = signaturePosition;
	}

	public PdfMarkPosition getAuthenticationStampPosition() {
		return authenticationStampPosition;
	}

	public void setAuthenticationStampPosition(PdfMarkPosition authenticationStampPosition) {
		this.authenticationStampPosition = authenticationStampPosition;
	}
}
