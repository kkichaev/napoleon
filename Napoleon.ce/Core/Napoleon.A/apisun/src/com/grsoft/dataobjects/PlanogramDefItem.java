package com.grsoft.dataobjects;

import com.grsoft.database.BlobSource;

public class PlanogramDefItem extends DataObject {
	@BlobSource
	public byte[] photo = null;
	public String name = "";
}
