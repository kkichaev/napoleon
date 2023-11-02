package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="agentplan", keyFields="begin")
@ServerInfo(name="AgentPlan")
public class AgentPlan extends DataObject{
	public Date begin;
	public Date end;
	
	public List<AgentPlanItem> groups = new ArrayList<AgentPlanItem>();
}
