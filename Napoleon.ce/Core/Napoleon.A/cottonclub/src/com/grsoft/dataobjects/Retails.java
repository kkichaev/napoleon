package com.grsoft.dataobjects;

import java.util.HashMap;

import com.grsoft.database.DbReader;
import com.grsoft.database.TableInfo;

@TableInfo(name="retails", keyFields="id")
public class Retails extends DataObject implements Comparable<Retails> {
	public String id;
	public String name;
	
	public static HashMap<String, Retails> getRetails() {
		HashMap<String, Retails> ret = new HashMap<String, Retails>();
				
		Retails data = new Retails();
		String table = DataObjectInfo.getInstance().getTableName(Retails.class);
		DbReader r = new DbReader();
		boolean bdo = r.select(data, table, null);
		while( bdo ) {
			ret.put(data.id, data);
			
			data = new Retails();
			bdo = r.selectNext(data);
		}
		r.close();
		return ret;
	}

	@Override
	public int compareTo(Retails another) {
		return name.compareTo(another.name);
	}
}
