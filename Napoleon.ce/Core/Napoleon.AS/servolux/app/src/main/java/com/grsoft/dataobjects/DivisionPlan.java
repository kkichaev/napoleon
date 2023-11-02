package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="DivisionPlan", keyFields="date,firm,isMonthly")
@ServerInfo(name="DivisionPlan")
public class DivisionPlan extends AgentPlanNew {

}
