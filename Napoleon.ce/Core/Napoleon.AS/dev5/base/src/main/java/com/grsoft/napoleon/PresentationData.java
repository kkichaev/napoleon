package com.grsoft.napoleon;
import com.grsoft.aceteam.R;

public class PresentationData {
	public long rowid;
	public int folder;
	public String name;
	public String image;
	public String id;
	public PresentationData(long rowid, int folder, String name, String image, String id) {
		this.rowid = rowid;
		this.folder = folder;
		this.name = name;
		this.image = image;
		this.id = id;
	}
}
