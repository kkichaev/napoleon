package com.grsoft.ads.dataobjects;

import com.grsoft.ads.utils.ConfigReader;
import com.grsoft.database.DbReader;
import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.napoleon.util.ConfigManager;

@TableInfo(name="AgentPrefix",keyFields="id")
public class AgentPrefix extends DataObject {
	public String id = "";
	public String login = "";
	public String password = "";
	public String prefix = "";
	
	public static AgentPrefix get(){
		ConfigReader config = (ConfigReader) ConfigManager.getConfig();
		AgentPrefix ap = new AgentPrefix();
		DbReader r = new DbReader();
		String table = DataObjectInfo.getInstance().getTableName(ap.getClass());
		
		boolean bdo = r.select(ap, table, "login='" + 
				config.getLogin() + "' and password='" + 
				config.getPassword() + "'" );
		
		r.close();
		
		return bdo ? ap : null;
	}
}
