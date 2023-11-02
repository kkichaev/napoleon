package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="TradeAction", keyFields="id")
@ServerInfo(name="TradeAction")
public class TradeAction extends DataObject {
	public String id = "";

	public Date start = new Date();
	public Date end = new Date();
	public Date startAction = new Date();
	public Date endAction = new Date();
	
	public List<TradeActionOrg> orgs = new ArrayList<TradeActionOrg>();
	public List<TradeActionOrg> stores = new ArrayList<TradeActionOrg>();

	public List<TradeActionItem> items = new ArrayList<TradeActionItem>();

	public boolean contains(OrgEx oe) {
		for(TradeActionOrg to : orgs)
			if(to.id.equals(oe.ido))
				return true;
		for(TradeActionOrg to : stores)
			if(to.id.equals(oe.id))
				return true;
		return false;
	}
}
