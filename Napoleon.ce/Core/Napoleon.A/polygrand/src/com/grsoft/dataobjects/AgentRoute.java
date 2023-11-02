package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="agentroute", keyFields="date")
public class AgentRoute extends DataObject{
	public Date date;
	public List<AgentRouteItem> items = new ArrayList<AgentRouteItem>();
	public int params;
	public Date changed;
}
