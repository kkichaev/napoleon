package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@ServerInfo(name="VisitCloudResponse")
@TableInfo(name="VisitCloudResponse", keyFields="created")
public class VisitCloudResponse extends DataObject {
	public Date created;
	public int changes;
	public int code = 0;
	public String answ = "";

	public List<VCRMissing> missing = new ArrayList<VCRMissing>();
}
