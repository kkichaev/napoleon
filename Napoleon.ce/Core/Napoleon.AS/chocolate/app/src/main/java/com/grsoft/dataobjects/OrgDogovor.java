package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.FieldOrder;

@TableInfo(name="dogovors", keyFields="ido,id")
public class OrgDogovor extends DataObject {
	@FieldOrder(order=0)
	public String id;
	
	@FieldOrder(order=1)
	public int firm;

	@FieldOrder(order=2)
	public String name;

	@FieldOrder(order=3)
	public String ido;
}
