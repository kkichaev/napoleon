package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import java.util.Date;

import com.grsoft.database.DbReader;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;

@TableInfo(name="AgentPrefix",keyFields="id")
@ServerInfo(name = "AgentPrefix")
public class AgentPrefix extends Agent {
	public String prefix = "";
	public String ser = "";
	public String number = "";
	public String region = "";
	public Date data = new Date();
	public String fullname = "";
	public String userid = "";
	public String order = "";
	
	public static AgentPrefix get(){
		Class<? extends DataObject> ac = DbObject.getDataType(AgentPrefix.class);
		
		AgentPrefix ap;
		try {
			ap = (AgentPrefix) ac.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			ap = new AgentPrefix();
		}
		DbReader r = new DbReader();
		String table = DataObjectInfo.getInstance().getTableName(ap.getClass());

		boolean bdo;
		bdo = r.select(ap, table, "id=userid");
		
		if( !bdo ) {
			Config config = ConfigManager.getConfig();
			bdo = r.select(ap, table, "login='" + 
					config.login + "' and password='" + 
					config.passw + "'" );
		}
		r.close();
		
		return bdo ? ap : null;
	}
}
