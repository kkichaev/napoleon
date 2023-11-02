package com.grsoft.dataobjects.impl;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.ChatAgent;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;


public class ChatAgentImpl extends DbObject<ChatAgent>{
	
	public static String getMyid(){
		Class<? extends DataObject> ac = DbObject.getDataType(ChatAgent.class);
		
		ChatAgent ap;
		try {
			ap = (ChatAgent) ac.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			ap = new ChatAgent();
		}
		DbReader r = new DbReader();
		String table = DataObjectInfo.getInstance().getTableName(ap.getClass());
		r.select(ap, table, "id=userid");
		r.close();
		
		return ap.id;
		
	}
}
