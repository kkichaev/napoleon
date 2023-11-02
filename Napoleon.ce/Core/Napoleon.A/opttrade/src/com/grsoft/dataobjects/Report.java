package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="Report", keyFields="name")
public class Report extends DataObject {
	public String name;
	public String encode;
	public byte[] report;
	
	@Override
	public String toString() {
		return name;
	}
}
