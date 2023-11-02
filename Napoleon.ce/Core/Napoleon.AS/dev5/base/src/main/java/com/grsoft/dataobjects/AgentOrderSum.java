package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import java.util.Date;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="agentordersum", keyFields="date")
@ServerInfo(name="AgentOrderSum")
public class AgentOrderSum extends DataObject {
	public Date date;
	
	@Scale(value=Consts.SUM_SCALE)
	public long sum;
}
