package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.FieldOrder;

@TableInfo(name="dogovors", keyFields="idDog", indexes="ido")
public class OrgDogovor extends DataObject {
	@FieldOrder(order=1)
	public String idDog;

	@FieldOrder(order=2)
	public String ido;
	
	@FieldOrder(order=3)
	public String name;

	@FieldOrder(order=4)
	public String supplyercode;
	
	@FieldOrder(order=5)
	public int delay;
	
	@Override
	public String toString() {
		return name;
	}
}
