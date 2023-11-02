package com.grsoft.dataobjects;

import com.grsoft.database.BlobSource;
import com.grsoft.types.FieldOrder;

public class DisplayItem extends DataObject {
	@BlobSource
	@FieldOrder(order=0)
	public byte[] id;
	
	@FieldOrder(order=1)
	public String folder;
}
