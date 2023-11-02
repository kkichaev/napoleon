package com.grsoft.dataobjects;

public class UnitEx extends UnitItem {
	public UnitEx(){}
	
	public UnitEx(UnitItem u) {
		id = u.id;
		name = u.name;
		inpack = u.inpack;
	}
	
	@Override public String toString() { return name; }
}
