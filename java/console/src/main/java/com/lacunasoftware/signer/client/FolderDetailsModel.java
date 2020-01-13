package com.lacunasoftware.signer.client;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class FolderDetailsModel {
	private UUID id;
	private String name;
	private List<FolderAccessModel> folderAccesses;

	FolderDetailsModel(LacunaSignerApiFoldersFolderDetailsModel model) {
		this.id = model.getId();
		this.name = model.getName();
		this.folderAccesses = new ArrayList<>();

		if (model.getFolderAccesses() != null) {
			for (LacunaSignerApiFoldersFolderAccessModel f : model.getFolderAccesses()) {
				this.folderAccesses.add(new FolderAccessModel(f));
			}
		}
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

	public List<FolderAccessModel> getFolderAccesses() {
		return folderAccesses;
	}

	public void setFolderAccesses(List<FolderAccessModel> folderAccesses) {
		this.folderAccesses = folderAccesses;
	}
}
