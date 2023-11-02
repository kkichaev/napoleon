package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.FieldOrder;

@TableInfo(name="dogovors", keyFields="id")
public class OrgDog extends DataObject {
	@FieldOrder(order=0)
	public String id;

	@FieldOrder(order=1)
	public String name;

	@FieldOrder(order=2)
	public String firm;

	@FieldOrder(order=3)
	public String ido;
	
	@FieldOrder(order = 4)	
	public int delay;

	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof OrgDog) ? id.equals(((OrgDog)obj).id) : false;
	}
}
