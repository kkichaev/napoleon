package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="agentakbdata", keyFields="id")
@ServerInfo(name="AgentAKBData")
public class AgentAKBData extends DataObject{
	public String id  = "";
	public int alldoc = 0;
	public int inroute = 0;
			
}
