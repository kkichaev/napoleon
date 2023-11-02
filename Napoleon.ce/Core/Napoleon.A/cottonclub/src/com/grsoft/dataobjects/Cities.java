package com.grsoft.dataobjects;

import java.util.HashMap;

import com.grsoft.database.DbReader;
import com.grsoft.database.TableInfo;

@TableInfo(name="cities", keyFields="id")
public class Cities extends DataObject implements Comparable<Cities> {
	public String id;
	public String name;
	
	public static HashMap<String, Cities> getCities() {
		HashMap<String, Cities> ret = new HashMap<String, Cities>();
		
		String table = DataObjectInfo.getInstance().getTableName(Cities.class);
		Cities data = new Cities(); 
		DbReader r = new DbReader();
		boolean bdo = r.select(data, table, null);
		while(bdo) {
			ret.put(data.id, data);
			data = new Cities();
			
			bdo = r.selectNext(data);
		}
		r.close();
		return ret;
	}

	@Override
	public int compareTo(Cities another) {
		return name.compareTo(another.name);
	}
}
