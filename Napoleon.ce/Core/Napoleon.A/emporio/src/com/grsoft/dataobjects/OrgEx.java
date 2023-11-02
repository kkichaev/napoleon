package com.grsoft.dataobjects;

import java.util.HashSet;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.util.Util;

public class OrgEx extends Org {
	public boolean isStopList() {
		if( stopped == null ) {
			stopped = new HashSet<String>();
			
			DbWriter.checkDBTable(Delivery.class);
			OrgStop data = new OrgStop();
			DbReader r = new DbReader();
			StringBuilder sb = new StringBuilder();
			sb.append("sumD > 0 and payDate < ").append(Util.getDate().getTime());
			boolean bdo = r.select(data, DataObjectInfo.getInstance().getTableName(Delivery.class), sb.toString());
			while(bdo) {
				stopped.add(data.id);
				bdo = r.selectNext(data);
			}
			r.close();
		}
		
		return stopped.contains(id); 
	}
}
