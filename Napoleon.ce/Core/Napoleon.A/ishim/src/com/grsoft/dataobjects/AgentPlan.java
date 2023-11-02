package com.grsoft.dataobjects;

import java.util.Date;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="agentPlans")
public class AgentPlan extends DataObject {
	public Date begin;
	public Date end;
	
	public List<AgentPlanItem> items;
}
