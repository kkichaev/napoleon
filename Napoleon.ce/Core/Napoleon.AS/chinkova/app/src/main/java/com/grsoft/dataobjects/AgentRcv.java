package com.grsoft.dataobjects;

import com.grsoft.database.DbReader;
import com.grsoft.database.TableInfo;

@TableInfo(name="AgentRcv",keyFields="id")
public class AgentRcv extends Agent {
	public String userid;
	
	public static AgentRcv currentAgent() {
		AgentRcv a = new AgentRcv();
		String table = DataObjectInfo.getInstance().getTableName(AgentRcv.class);
		DbReader r = new DbReader();
		boolean bdo = r.select(a, table, "id = userid");
		r.close();
		
		return bdo ? a : null;
	}
}
