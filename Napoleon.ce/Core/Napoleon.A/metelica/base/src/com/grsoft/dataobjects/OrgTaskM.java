package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.TableInfo;

@TableInfo(name="agentOrgTask", keyFields="id")
public class OrgTaskM extends DataObject {
	public String id = "";
    public String orgid = "";
    public Date start;
    public Date finish;
    public String text = "";
}
