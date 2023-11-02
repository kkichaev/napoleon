package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="orgcontract", keyFields="id")
@ServerInfo(name="OrgContract")
public class OrgContract extends DataObject {
	public String id = "";
	
	public List<OrgContractItem> items = new ArrayList<OrgContractItem>();
}
