package com.lacunasoftware.signer.client;


import java.util.ArrayList;
import java.util.List;


public class FlowActionCreateModel {
	private FlowActionType type;
	private int step;
	private ParticipantUserModel user;
	private int numberRequiredSignatures;
	private String ruleName;
	private List<ParticipantUserModel> signRuleUsers;
	private boolean allowElectronicSignature;

	public FlowActionType getType() {
		return type;
	}

	public void setType(FlowActionType type) {
		this.type = type;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
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

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public List<ParticipantUserModel> getSignRuleUsers() {
		return signRuleUsers;
	}

	public void setSignRuleUsers(List<ParticipantUserModel> signRuleUsers) {
		this.signRuleUsers = signRuleUsers;
	}

	public boolean isAllowElectronicSignature() {
		return allowElectronicSignature;
	}

	public void setAllowElectronicSignature(boolean allowElectronicSignature) {
		this.allowElectronicSignature = allowElectronicSignature;
	}

	LacunaSignerApiFlowActionsFlowActionCreateModel toModel() {
		LacunaSignerApiFlowActionsFlowActionCreateModel model = new LacunaSignerApiFlowActionsFlowActionCreateModel();
		model.setType(LacunaSignerApiFlowActionsFlowActionCreateModel.TypeEnum.valueOf(type.name()));
		model.setStep(step);
		model.setNumberRequiredSignatures(numberRequiredSignatures);
		model.setRuleName(ruleName);
		model.setAllowElectronicSignature(allowElectronicSignature);

		if (user != null) {
			model.setUser(user.toModel());
		}

		if (signRuleUsers != null) {
			List<LacunaSignerApiUsersParticipantUserModel> models = new ArrayList<>();
			for (ParticipantUserModel s : signRuleUsers) {
				models.add(s.toModel());
			}
			model.setSignRuleUsers(models);
		}
		return model;
	}
}
