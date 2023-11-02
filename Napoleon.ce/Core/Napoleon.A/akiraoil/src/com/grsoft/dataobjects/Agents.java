package com.grsoft.dataobjects;

import com.grsoft.database.DbReader;
import com.grsoft.database.TableInfo;

@TableInfo(name="Agents", keyFields="id")
public class Agents extends DataObject {
	public int price = 0;
	public String id = "";
	public int dealer = 0;
	
	public static int getPriceIndex() {
		int index = 0;
		
		Agents a = new Agents();
		DbReader r = new DbReader();
		if(r.select(a, a.getTableName(), "")) {
			index = a.price;
		}
		r.close();
		return index;
	}
	
	static public boolean isDealer() {
		boolean ret = false;
		Agents a = new Agents();
		DbReader r = new DbReader();
		if(r.select(a, a.getTableName(), "")) {
			ret = a.dealer > 0;
		}
		r.close();
		return ret;
	}
}
