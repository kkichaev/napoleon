package com.grsoft.dataobjects;

import com.grsoft.database.BlobSource;
import com.grsoft.types.FieldOrder;

public class DispatchPhoto extends DataObject {
	@FieldOrder(order=0)
	public String number = "";
	
	@BlobSource
	@FieldOrder(order=1)
	public byte[] id;
}
