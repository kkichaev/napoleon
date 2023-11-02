package com.grsoft.dataobjects;

import com.grsoft.database.DbReader;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.FieldOrder;

@TableInfo(name="PriceSetQty", keyFields="id")
@ServerInfo(name="PriceSetQty")
public class PriceSetQty extends DataObject {
	@FieldOrder(order = 0)
	public String id = "";
	
	public static boolean needSetQty(String priceId) {
		DbReader r = new DbReader();
		PriceSetQty data = new PriceSetQty(); 
		boolean ret = r.select(data, data.getTableName(), "id='" + priceId + "'");
		r.close();
		
		return ret;
	}
}
