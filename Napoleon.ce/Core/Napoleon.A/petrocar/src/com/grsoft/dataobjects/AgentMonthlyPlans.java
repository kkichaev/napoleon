package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="AgentMonthlyPlans", keyFields="date")
@ServerInfo(name="PCAgentMonthlyPlans")
public class AgentMonthlyPlans extends DataObject {
	public Date date = new Date();

	@Scale(value=Consts.SUM_SCALE)
	public int plan = 0;
	
	@Scale(value=Consts.SUM_SCALE)
	public int pdz = 0;
}
