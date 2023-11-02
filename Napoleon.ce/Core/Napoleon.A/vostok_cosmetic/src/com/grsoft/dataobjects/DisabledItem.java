package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class DisabledItem extends DataObject {
	@FieldOrder(order=0)
	public String id;

	@FieldOrder(order=1)
	public int isFolder;
}
