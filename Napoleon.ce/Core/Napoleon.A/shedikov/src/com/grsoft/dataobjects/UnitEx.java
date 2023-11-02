package com.grsoft.dataobjects;

public class UnitEx extends UnitItem {
	public UnitEx(UnitItem u) {
		id = u.id;
		name = u.name;
		coef = u.coef;
	}
	
	@Override public String toString() { return name; }
}
