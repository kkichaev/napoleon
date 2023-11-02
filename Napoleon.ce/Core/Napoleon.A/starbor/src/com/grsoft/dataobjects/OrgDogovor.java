package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.FieldOrder;

@TableInfo(name="dogovors", keyFields="id", indexes="ido")
public class OrgDogovor extends DataObject {
	@FieldOrder(order=0)
	public String name;

	@FieldOrder(order=1)
	public String ido;

	@FieldOrder(order=1)
	public String id;

	@FieldOrder(order=2)
	public int costType;
	
	@FieldOrder(order=3)
	public String firm;

	@Override
	public String toString() {
		return name;
	}
}
