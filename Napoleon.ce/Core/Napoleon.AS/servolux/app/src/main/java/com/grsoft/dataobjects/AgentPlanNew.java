package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="PlanNew", keyFields="date,firm,isMonthly")
public class AgentPlanNew extends DataObject {
	public Date date;
	public String firm;
	public int isMonthly;
	
	public List<AgentPlanItem> items = new ArrayList<AgentPlanItem>();
}
