package com.grsoft.dataobjects.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.AgentsName;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.VisitItemEx;

public class VisitImplEx extends VisitImpl {
	
	@Override
	public void addPhoto(byte[] photo) {
		OrgImpl org = new OrgImpl();
		Org o = org.getData();
		AgentsName agent = new AgentsName();
		
		o.id = data.id;
		org.read();
		org.close();
		
		String table = DataObjectInfo.getInstance().getTableName(agent.getClass()); 
		DbReader r = new DbReader();
		r.select(agent, table, "userid=id");
		r.close();

		String itemName = makeItemName(agent, o);
		VisitItemEx visitItem = new VisitItemEx();
		visitItem.id = photo;
		visitItem.name = itemName;
		data.items.add(visitItem);
		write();
		close();
	}

	String stripString(String str) {
		StringBuilder res = new StringBuilder();
		for( int i=0; i<str.length(); i++ ) {
			char sym = str.charAt(i);
			res.append(Character.isLetterOrDigit(sym) ? sym : '_');
			
		}
		return res.toString();
	}
	
	String makeItemName(AgentsName agent, Org o) {
		String res = stripString(agent.name);
		res += "\\";
		res += stripString(o.name);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
		res += "\\";
		res += sdf.format(new Date()) + ".jpg";
		return res;
	}
}
