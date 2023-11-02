package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.FieldOrder;

@TableInfo(name="Agreement", keyFields="id")
@ServerInfo(name="Agreement")
public class Agreement extends DataObject {
	@FieldOrder(order = 0)
	public String id = "";
	
	@FieldOrder(order = 1)
	public String name = "";

	@FieldOrder(order = 2)
	public String firm = "";

	@FieldOrder(order = 3)
	public String idSeg = "";
	
	@Override public String toString() { return name; }
}
