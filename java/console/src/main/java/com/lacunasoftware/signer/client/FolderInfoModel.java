package com.lacunasoftware.signer.client;


import java.util.UUID;


public class FolderInfoModel {
	private UUID id;
	private String name;

	FolderInfoModel(LacunaSignerApiFoldersFolderInfoModel model) {
		this.id = model.getId();
		this.name = model.getName();
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
