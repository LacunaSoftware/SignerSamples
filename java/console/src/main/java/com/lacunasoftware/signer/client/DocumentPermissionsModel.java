package com.lacunasoftware.signer.client;


public class DocumentPermissionsModel {
	private boolean move;
	private boolean editFlow;

	DocumentPermissionsModel(LacunaSignerApiDocumentsDocumentPermissionsModel model) {
		this.move = model.isMove();
		this.editFlow = model.isEditFlow();
	}

	public boolean isMove() {
		return move;
	}

	public void setMove(boolean move) {
		this.move = move;
	}

	public boolean isEditFlow() {
		return editFlow;
	}

	public void setEditFlow(boolean editFlow) {
		this.editFlow = editFlow;
	}
}
