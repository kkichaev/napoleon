package com.grsoft.dataobjects;

import java.util.HashMap;
import java.util.Map;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="Inventory", keyFields="id")
@ServerInfo(name="Inventory")
public class Inventory extends DataObject {
	public String id = "";
	public String name = "";
	
	public static Map<String, Inventory> get() {
		final Map<String, Inventory> ret = new HashMap<String, Inventory>();
		
		DataTraveler.travel(Inventory.class, new DataTraveler.Travel<Inventory>(true) {

			@Override
			public boolean travel(DataTraveler<Inventory> item) {
				ret.put(item.data.id, item.data);
				return true;
			}
			
		}, "");
		
		return ret;
	}
}
