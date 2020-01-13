package com.lacunasoftware.signer.client;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class FolderAccessModel {
	private UUID id;
	private UUID userId;
	private List<Roles> folderRoles;
	private List<Roles> organizationRoles;
	private String userName;
	private String userIdentifier;

	FolderAccessModel(LacunaSignerApiFoldersFolderAccessModel model) {
		this.id = model.getId();
		this.userId = model.getUserId();
		this.folderRoles = new ArrayList<>();
		this.organizationRoles = new ArrayList<>();
		this.userName = model.getUserName();
		this.userIdentifier = model.getUserIdentifier();

		if (model.getFolderRoles() != null) {
			for (LacunaSignerApiFoldersFolderAccessModel.Roles r : model.getFolderRoles()) {
				this.folderRoles.add(Roles.valueOf(r.name()));
			}
		}

		if (model.getOrganizationRoles() != null) {
			for (LacunaSignerApiFoldersFolderAccessModel.Roles r : model.getOrganizationRoles()) {
				this.folderRoles.add(Roles.valueOf(r.name()));
			}
		}
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	public List<Roles> getFolderRoles() {
		return folderRoles;
	}

	public void setFolderRoles(List<Roles> folderRoles) {
		this.folderRoles = folderRoles;
	}

	public List<Roles> getOrganizationRoles() {
		return organizationRoles;
	}

	public void setOrganizationRoles(List<Roles> organizationRoles) {
		this.organizationRoles = organizationRoles;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserIdentifier() {
		return userIdentifier;
	}

	public void setUserIdentifier(String userIdentifier) {
		this.userIdentifier = userIdentifier;
	}
}
