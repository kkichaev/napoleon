package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="orgtypes", keyFields="id")
@ServerInfo(name="OrgType")
public class OrgType extends DataObject {
	public String id = "";
	public String name = ""; 
	public List<OrgTypeItem> items = new ArrayList<OrgTypeItem>();
}
