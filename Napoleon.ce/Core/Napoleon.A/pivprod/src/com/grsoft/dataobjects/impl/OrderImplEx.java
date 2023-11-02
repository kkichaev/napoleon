package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderEx;

public class OrderImplEx extends OrderImpl {
	@Override
	public String getDescription(Context context) {
		StringBuilder sb = new StringBuilder();
		sb.append(super.getDescription(context));
		
		
		AgentPrefix ap = new AgentPrefix();
		DbReader r = new DbReader();
		String table = DataObjectInfo.getInstance().getTableName(ap.getClass());

		boolean bdo;
		bdo = r.select(ap, table, "id != userid and id = '" + ((OrderEx)data).userid + "'");
		
		if( bdo ) {
			sb.append("<br>");
			sb.append(ap.name);
		}
		
		r.close();
		
		return sb.toString();
	}
}
