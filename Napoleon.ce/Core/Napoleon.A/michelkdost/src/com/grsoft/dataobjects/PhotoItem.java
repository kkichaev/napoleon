package com.grsoft.dataobjects;

import com.grsoft.database.BlobSource;
import com.grsoft.types.FieldOrder;

public class PhotoItem extends DataObject {
	@BlobSource
	@FieldOrder(order=0)
	public byte[] id;
}
