package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class DistrItem extends DataObject {
	public DistrItem(int pos, String id, String name) {
		number = pos;
		this.id = id;
		this.name = name;
		exists = 0;
	}
	
	public DistrItem() {}

	@FieldOrder(order=0)
	public int number;
	
	@FieldOrder(order=1)
	public String id;
	
	@FieldOrder(order=2)
	public String name;
	
	@FieldOrder(order=3)
	public int exists;
}
