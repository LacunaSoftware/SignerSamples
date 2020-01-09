package com.lacunasoftware.signer.client;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class FlowActionModel {
	private UUID id;
	private FlowActionType type;
	private ActionStatus status;
	private int step;
	private Date creationDate;
	private Date pendingDate;
	private Date updateDate;
	private ParticipantUserModel user;
	private int numberRequiredSignatures;
	private List<SignRuleUserModel> signRuleUsers;
	private boolean allowElectronicSignature;

	FlowActionModel(LacunaSignerApiFlowActionsFlowActionModel model) {
		this.id = model.getId();
		this.step = model.getStep();
		this.numberRequiredSignatures = model.getNumberRequiredSignatures();
		this.allowElectronicSignature = model.isAllowElectronicSignature();
		this.creationDate = model.getCreationDate();
		this.pendingDate = model.getPendingDate();
		this.updateDate = model.getUpdateDate();

		if (model.getType() != null) {
			this.type = FlowActionType.valueOf(model.getType().name());
		}
		if (model.getStatus() != null) {
			this.status = ActionStatus.valueOf(model.getStatus().name());
		}
		if (model.getUser() != null) {
			this.user = new ParticipantUserModel(model.getUser());
		}
		if (model.getSignRuleUsers() != null) {
			List<SignRuleUserModel> userModels = new ArrayList<>();
			for (LacunaSignerApiFlowActionsSignRuleUserModel m : model.getSignRuleUsers()) {
				userModels.add(new SignRuleUserModel(m));
			}
			this.signRuleUsers = userModels;
		}
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public FlowActionType getType() {
		return type;
	}

	public void setType(FlowActionType type) {
		this.type = type;
	}

	public ActionStatus getStatus() {
		return status;
	}

	public void setStatus(ActionStatus status) {
		this.status = status;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getPendingDate() {
		return pendingDate;
	}

	public void setPendingDate(Date pendingDate) {
		this.pendingDate = pendingDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public ParticipantUserModel getUser() {
		return user;
	}

	public void setUser(ParticipantUserModel user) {
		this.user = user;
	}

	public int getNumberRequiredSignatures() {
		return numberRequiredSignatures;
	}

	public void setNumberRequiredSignatures(int numberRequiredSignatures) {
		this.numberRequiredSignatures = numberRequiredSignatures;
	}

	public List<SignRuleUserModel> getSignRuleUsers() {
		return signRuleUsers;
	}

	public void setSignRuleUsers(List<SignRuleUserModel> signRuleUsers) {
		this.signRuleUsers = signRuleUsers;
	}

	public boolean isAllowElectronicSignature() {
		return allowElectronicSignature;
	}

	public void setAllowElectronicSignature(boolean allowElectronicSignature) {
		this.allowElectronicSignature = allowElectronicSignature;
	}
}
