package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="RepDef", keyFields="id")
public class ReportDef extends DataObject {
	public String id;
	public String name;
}
