package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="orgInfo", keyFields="id,sectionID,paramID")
public class OrgInfoData extends DataObject {
	public String id = "";
	public int sectionID;
	public int paramID;
	public String sectionName = "";
	public String paramName = "";
	public String paramValue = "";
}
