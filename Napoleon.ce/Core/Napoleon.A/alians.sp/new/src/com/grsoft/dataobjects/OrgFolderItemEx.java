package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class OrgFolderItemEx extends OrgFolderItem {
	
	@FieldOrder(order=3)
	public String time;
	
	@FieldOrder(order=4)
	public int everyday = 0;
}
