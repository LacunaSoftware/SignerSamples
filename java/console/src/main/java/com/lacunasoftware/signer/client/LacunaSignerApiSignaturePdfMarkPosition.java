package com.lacunasoftware.signer.client;


import com.google.gson.annotations.SerializedName;


public class LacunaSignerApiSignaturePdfMarkPosition {

	@SerializedName("page")
	private int page;

	@SerializedName("topLeftX")
	private double topLeftX;

	@SerializedName("topLeftY")
	private double topLeftY;

	@SerializedName("width")
	private double width;

	@SerializedName("height")
	private double height;

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public double getTopLeftX() {
		return topLeftX;
	}

	public void setTopLeftX(double topLeftX) {
		this.topLeftX = topLeftX;
	}

	public double getTopLeftY() {
		return topLeftY;
	}

	public void setTopLeftY(double topLeftY) {
		this.topLeftY = topLeftY;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}
}
