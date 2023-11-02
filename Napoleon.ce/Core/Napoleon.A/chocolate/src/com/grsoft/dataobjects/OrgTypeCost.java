package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.FieldOrder;

@TableInfo(name="orgCost", keyFields="ido,folder")
public class OrgTypeCost extends DataObject {
	@FieldOrder(order=0)
	public int type;
	
	@FieldOrder(order=1)
	public String folder;

	@FieldOrder(order=2)
	public String ido;
}
