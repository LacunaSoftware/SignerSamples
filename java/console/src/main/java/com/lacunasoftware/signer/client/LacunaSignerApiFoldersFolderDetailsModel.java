package com.lacunasoftware.signer.client;


import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.UUID;


public class LacunaSignerApiFoldersFolderDetailsModel {

	@SerializedName("id")
	private UUID id;

	@SerializedName("name")
	private String name;

	@SerializedName("folderAccesses")
	private List<LacunaSignerApiFoldersFolderAccessModel> folderAccesses;

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

	public List<LacunaSignerApiFoldersFolderAccessModel> getFolderAccesses() {
		return folderAccesses;
	}

	public void setFolderAccesses(List<LacunaSignerApiFoldersFolderAccessModel> folderAccesses) {
		this.folderAccesses = folderAccesses;
	}
}
