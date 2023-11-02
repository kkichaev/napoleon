package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.FieldVersion;

public class ContractOrgImg extends DataObject {
	@FieldOrder(order = 0)
	public String id = "";

//	@FieldOrder(order = 1)
//	public byte[] photo = null;

	@FieldOrder(order = 2)
	public String name = "";
	
	@FieldOrder(order = 3)
	@FieldVersion(version=1)
	public String href = "";
}
