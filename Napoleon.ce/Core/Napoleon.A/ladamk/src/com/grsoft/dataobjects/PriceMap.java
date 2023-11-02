package com.grsoft.dataobjects;

import java.util.HashMap;

import com.grsoft.database.DbReader;

public class PriceMap extends HashMap<String, Price> {
	DbReader reader = new DbReader();
	
	private static final long serialVersionUID = 1L;

	public void close() {
		reader.close();
	}
	
	public Price get(String id) {
		Price ret = super.get(id);
		if(ret == null) {
			ret = new Price();
			String where = "id='" + id + "'";
			reader.select(ret, ret.getTableName(), where);
			put(id, ret);
		}
		
		return ret;
	}
}
