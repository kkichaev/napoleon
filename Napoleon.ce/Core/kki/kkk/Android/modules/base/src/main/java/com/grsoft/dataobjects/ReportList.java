package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@ServerInfo(name="ReportList")
@TableInfo(name="reportList", keyFields="id")
public class ReportList extends DataObject {
	public String id = "";
	public String name = "";
}
