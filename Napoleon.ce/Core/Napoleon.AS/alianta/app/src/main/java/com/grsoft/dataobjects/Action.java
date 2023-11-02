 package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="Actions", keyFields="id")
@ServerInfo(name="Actions")
public class Action extends DataObject {
	public String id = "";
	
	public String cfo = "";	
	public String name = "";
	
	public Date start = new Date();
	public Date end = new Date();
	
	public int isManual = 0;
	
	public int costype = 0;
	
	public List<ActionItem> items = new ArrayList<ActionItem>();
	public List<ActionOrg> orgs = new ArrayList<ActionOrg>();
	
	public boolean canApply(OrgEx oe) {
		if(costype != 0 && oe.costype != (costype-1))
			return false;

		for(ActionOrg ao : orgs) {
			String id = ao.id + "\t";
			if(oe.id.startsWith(id))
				return true;
		}
		if(orgs.size() > 0)
			return false;

		if(cfo.equals(oe.cfo))
			return true;
		
		return false;
	}
}
