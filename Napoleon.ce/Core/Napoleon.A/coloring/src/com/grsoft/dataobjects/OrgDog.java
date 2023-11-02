package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class OrgDog extends DataObject {
	
	public OrgDog(){
		
	}
	
	public OrgDog(String id, String name, int costype){
		this.id=id;
		this.name=name;
		this.costype=costype;
	}
	@FieldOrder(order=0)
	public String id;

	@FieldOrder(order=1)
	public String name;
	
	@FieldOrder(order=2)
	public int costype;
	
	@Override
	public String toString() {
		return name;
	}
}
