package com.grsoft.napoleon;

public class PresentationDataEx extends PresentationData {
	public String desc = "";
	public PresentationDataEx(long rowid, int folder, String name, String image, String desc) {
		super(rowid, folder, name, image);
		this.desc = desc;
	}

}
