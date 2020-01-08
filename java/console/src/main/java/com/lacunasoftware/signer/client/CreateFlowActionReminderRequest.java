package com.lacunasoftware.signer.client;


import java.util.UUID;


public class CreateFlowActionReminderRequest {
	private UUID documentId;
	private UUID flowActionId;

	public UUID getDocumentId() {
		return documentId;
	}

	public void setDocumentId(UUID documentId) {
		this.documentId = documentId;
	}

	public UUID getFlowActionId() {
		return flowActionId;
	}

	public void setFlowActionId(UUID flowActionId) {
		this.flowActionId = flowActionId;
	}

	LacunaSignerApiNotificationsCreateFlowActionReminderRequest toModel() {
		LacunaSignerApiNotificationsCreateFlowActionReminderRequest model = new LacunaSignerApiNotificationsCreateFlowActionReminderRequest();
		model.setDocumentId(documentId);
		model.setFlowActionId(flowActionId);
		return model;
	}
}
