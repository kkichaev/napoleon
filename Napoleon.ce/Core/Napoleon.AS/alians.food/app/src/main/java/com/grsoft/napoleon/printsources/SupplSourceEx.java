package com.grsoft.napoleon.printsources;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.DataObjectInfo;

public class SupplSourceEx extends SupplSource {
	@PrintInfo(name="Приказ")
	public String order = "";
	
	@PrintInfo(name="ИмяАгента")
	public String agentName = "";
	
	
	@Override
	public void setSupplyer(String code) {
		super.setSupplyer(code);
		
		AgentPrefix a = new AgentPrefix();
		String table = DataObjectInfo.getInstance().getTableName(AgentPrefix.class);
		DbReader r = new DbReader();
		if( r.select(a, table, "id=userid") ) {
			if( a.order.length() > 0 ) {
				order = a.order;
				if(certificate.length() > 0 )
					nameIP = a.name;
				else {
					chief = a.name;
					buh = a.name;
				}
			}
			agentName = a.name;
		}
		r.close();
	}
}
