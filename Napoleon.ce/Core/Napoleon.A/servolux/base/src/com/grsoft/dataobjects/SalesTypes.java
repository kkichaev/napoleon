package com.grsoft.dataobjects;

import java.util.HashMap;
import java.util.Map;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="slsTypes", keyFields="id")
@ServerInfo(name="SalesTypes")
public class SalesTypes extends DataObject implements Comparable<SalesTypes> {
	public String id = "";
	public String name = "";
	
	public static Map<String, SalesTypes> getSalesTypes() {
		final Map<String, SalesTypes> ret = new HashMap<String, SalesTypes>();
		
		DataTraveler.travel(SalesTypes.class, new DataTraveler.Travel<SalesTypes>(true) {

			@Override
			public boolean travel(DataTraveler<SalesTypes> item) {
				ret.put(item.data.id, item.data);
				return true;
			}
		}, "");
		
		return ret;
	}

	@Override
	public int compareTo(SalesTypes o) {
		return name.compareTo(o.name);
	}
}
