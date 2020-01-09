package com.lacunasoftware.signer.client;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class CreateDocumentRequest {
	private List<FileUploadModel> files;
	private UUID folderId;
	private List<FlowActionCreateModel> flowActions;
	private List<ObserverCreateModel> observers;
	private String newFolderName;

	public List<FileUploadModel> getFiles() {
		return files;
	}

	public void setFiles(List<FileUploadModel> files) {
		this.files = files;
	}

	public UUID getFolderId() {
		return folderId;
	}

	public void setFolderId(UUID folderId) {
		this.folderId = folderId;
	}

	public List<FlowActionCreateModel> getFlowActions() {
		return flowActions;
	}

	public void setFlowActions(List<FlowActionCreateModel> flowActions) {
		this.flowActions = flowActions;
	}

	public List<ObserverCreateModel> getObservers() {
		return observers;
	}

	public void setObservers(List<ObserverCreateModel> observers) {
		this.observers = observers;
	}

	public String getNewFolderName() {
		return newFolderName;
	}

	public void setNewFolderName(String newFolderName) {
		this.newFolderName = newFolderName;
	}

	LacunaSignerApiDocumentsCreateDocumentRequest toModel() {
		LacunaSignerApiDocumentsCreateDocumentRequest model = new LacunaSignerApiDocumentsCreateDocumentRequest();
		model.setFolderId(folderId);
		model.setNewFolderName(newFolderName);

		if (files != null) {
			List<LacunaSignerApiFileUploadModel> fums = new ArrayList<>();
			for (FileUploadModel m : files) {
				fums.add(m.toModel());
			}
			model.setFiles(fums);
		}

		if (flowActions != null) {
			List<LacunaSignerApiFlowActionsFlowActionCreateModel> facms = new ArrayList<>();
			for (FlowActionCreateModel f : flowActions) {
				facms.add(f.toModel());
			}
			model.setFlowActions(facms);
		}

		if (observers != null) {
			List<LacunaSignerApiObserversObserverCreateModel> ocms = new ArrayList<>();
			for (ObserverCreateModel o : observers) {
				ocms.add(o.toModel());
			}
			model.setObservers(ocms);
		}

		return model;
	}
}
