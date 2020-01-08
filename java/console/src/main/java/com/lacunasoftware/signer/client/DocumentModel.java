package com.lacunasoftware.signer.client;


import java.util.ArrayList;
import java.util.List;


public class DocumentModel extends DocumentInfoBaseModel {
	private String checksumMD5;
	private boolean isDeleted;
	private List<FlowActionModel> flowActions;
	private List<ObserverModel> observers;
	private DocumentPermissionsModel permissions;

	DocumentModel(LacunaSignerApiDocumentsDocumentModel model) {
		super(model);
		this.checksumMD5 = model.getChecksumMd5();
		this.isDeleted = model.isIsDeleted();

		if (model.getFlowActions() != null) {
			List<FlowActionModel> flowActionModels = new ArrayList<>();
			for (LacunaSignerApiFlowActionsFlowActionModel m : model.getFlowActions()) {
				flowActionModels.add(new FlowActionModel(m));
			}
			this.flowActions = flowActionModels;
		}
		if (model.getObservers() != null) {
			List<ObserverModel> observerModels = new ArrayList<>();
			for (LacunaSignerApiObserversObserverModel m : model.getObservers()) {
				observerModels.add(new ObserverModel(m));
			}
			this.observers = observerModels;
		}
		if (model.getPermissions() != null) {
			this.permissions = new DocumentPermissionsModel(model.getPermissions());
		}
	}

	public String getChecksumMD5() {
		return checksumMD5;
	}

	public void setChecksumMD5(String checksumMD5) {
		this.checksumMD5 = checksumMD5;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean deleted) {
		isDeleted = deleted;
	}

	public List<FlowActionModel> getFlowActions() {
		return flowActions;
	}

	public void setFlowActions(List<FlowActionModel> flowActions) {
		this.flowActions = flowActions;
	}

	public List<ObserverModel> getObservers() {
		return observers;
	}

	public void setObservers(List<ObserverModel> observers) {
		this.observers = observers;
	}

	public DocumentPermissionsModel getPermissions() {
		return permissions;
	}

	public void setPermissions(DocumentPermissionsModel permissions) {
		this.permissions = permissions;
	}
}
