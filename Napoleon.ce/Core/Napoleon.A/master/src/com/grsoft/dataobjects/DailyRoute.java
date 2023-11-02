package com.grsoft.dataobjects;

import java.util.Date;
import java.util.List;

import com.grsoft.database.TableInfo;


@TableInfo(name="DailyRoute", keyFields="date")
public class DailyRoute extends DataObject {
	public Date date;
	public List<DailyRouteItem> items;
	
	public boolean containsOrg(Org o) {
		if( items == null )
			return false;
		
		for(DailyRouteItem i : items) {
			if( i.id.equals(o.id) )
				return true;
		}
		
		return false;
	}
}
