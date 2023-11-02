package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="agentBalance", keyFields="userid")
@ServerInfo(name="AgentBalance")
public class AgentBalance extends DataObject {
	public String userid = "";
	
	@Scale(value=Consts.SUM_SCALE)
	public int balance = 0;

	public String uid = "";
}
