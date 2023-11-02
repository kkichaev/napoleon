package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import java.util.Date;

import com.grsoft.database.TableInfo;

@TableInfo(name="agentOrgTask", keyFields="id")
public class OrgTask extends DataObject {
	public String id = "";
    public String orgid = "";
    public Date start;
    public Date finish;
    public String text = "";
    public Date created;
    public String manager = "";
}
