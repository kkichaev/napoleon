package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;

@TableInfo(name="reportRequest", keyFields="id")
public class ReportRequest extends DataObject {
	public Date date = new Date();
	public Date sent = null;
	
	public String id = "";
	public String idOrg = "";
	
	public Date start = new Date();
	public Date end = new Date();
}
