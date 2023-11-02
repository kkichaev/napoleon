package com.grsoft.dataobjects;

import java.util.HashMap;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="stores", keyFields="id")
@ServerInfo(name="Stores")
public class Store extends DataObject {
	public String id = "";
	public String name = "";
	
	public static String MAIN_WH_ID = "MAIN";

	@Override public String toString() { return name; }
	
	public static HashMap<String, Store> load() {
		final HashMap<String, Store> ret = new HashMap<String, Store>();
		DataTraveler.travel(Store.class, new DataTraveler.Travel<Store>(true) {

			@Override
			public boolean travel(DataTraveler<Store> item) {
				ret.put(item.data.id, item.data);
				return true;
			}
		}, "");
		
		return ret;
	}
	
	public static Store mainStore() {
		Store ret = new Store();
		ret.id = MAIN_WH_ID;
		ret.name = "Основной склад";
		
		return ret;
	}
}
