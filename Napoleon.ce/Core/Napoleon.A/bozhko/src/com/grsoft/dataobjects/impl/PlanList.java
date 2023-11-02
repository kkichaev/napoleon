package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.Date;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.AgentSalesPlan;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.util.Util;

public class PlanList extends ArrayList<AgentSalesPlan> {
	private static final long serialVersionUID = 1L;
	
	Date dateStart;
	Date dateEnd;
	
	PlanList() {
		dateStart = Util.getDate();
		dateEnd = dateStart;
	}
	
	void load() {		
		AgentSalesPlan plan = new AgentSalesPlan();
		DbReader r = new DbReader();
		String table = DataObjectInfo.getInstance().getTableName(plan.getClass());
		String now = Long.toString(dateStart.getTime());
		String where = "dateStart <= " + now + " and dateEnd >= " + now;
		boolean bdo = r.select(plan, table, where);
		while(bdo) {
			add(plan);
			if( plan.dateStart.compareTo(dateStart) < 0 )
				dateStart = plan.dateStart;
			if( plan.dateEnd.compareTo(dateEnd) < 0 )
				dateEnd = plan.dateEnd;
			
			plan = new AgentSalesPlan();
			bdo = r.selectNext(plan);
		}
		r.close();
		
		// dateEnd установим на начало сл.дня
		dateEnd = new Date(dateEnd.getTime() + 3600000l * 24);
	}
}