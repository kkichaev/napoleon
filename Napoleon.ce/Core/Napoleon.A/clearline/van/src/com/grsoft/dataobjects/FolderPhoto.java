package com.grsoft.dataobjects;

import com.grsoft.database.BlobSource;

public class FolderPhoto extends DataObject {
	public String id = "";
	@BlobSource
	public byte[] pic;
	public String color = "";
	public int tsz = 0;
}
