package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="PriceTypes", keyFields="id")
public class PriceType extends DataObject {
	public String id;
	public String name;
	
	@Override
	public String toString() {
		return name;
	}
}
