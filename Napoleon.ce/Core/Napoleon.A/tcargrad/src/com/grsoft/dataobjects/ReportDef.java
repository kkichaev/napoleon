package com.grsoft.dataobjects;

import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="RepDef", keyFields="id")
public class ReportDef extends DataObject {
	public String id;
	public String name;
	public List<OrderProps> items;
}
