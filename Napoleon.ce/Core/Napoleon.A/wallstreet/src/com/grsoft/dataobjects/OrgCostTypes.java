package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="CostTypes", keyFields="id", indexes="idOrg")
@ServerInfo(name="CostTypes")
public class OrgCostTypes extends DataObject {
	public String id = "";
	public String idOrg = "";
	public String idCost = "";
	
	public int priority = 0;
	
	public Date start;
	public Date end;
}
