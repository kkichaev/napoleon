package com.grsoft.dataobjects;

import java.util.HashMap;
import java.util.Map;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="Brands", keyFields="id")
@ServerInfo(name="Brands")
public class Brands extends DataObject implements Comparable<Brands> {
	public String id = "";
	public String name = "";
	
	public static Map<String, Brands> get() {
		final Map<String, Brands> ret = new HashMap<String, Brands>();
		
		DataTraveler.travel(Brands.class, new DataTraveler.Travel<Brands>(true) {

			@Override
			public boolean travel(DataTraveler<Brands> item) {
				ret.put(item.data.id, item.data);
				return true;
			}
		}, "", "name");
		
		return ret;
	}

	@Override
	public int compareTo(Brands arg0) {
		return name.compareTo(arg0.name);
	}
}
