package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="agentroute", keyFields="id")
public class AgentRoute extends DataObject {
	public String id = "AgentRoute";
	public Date date;
	public int params;
	public List<AgentRouteItem> items = new ArrayList<AgentRouteItem>();
}
