package com.grsoft.dataobjects;

import com.grsoft.database.DbReader;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="priceTypes", keyFields="id")
@ServerInfo(name="PriceTypes")
public class PriceTypes extends DataObject {
	public String id = "";
	public String name = "";
	public int index;
//	public String idFirm = "";

	@Override public String toString() { return name; }
	
	static public int getPriceTypeColumn(String id) {
		int idx = 0;
		DbReader r = new DbReader();
		PriceTypes pt = new PriceTypes();
		boolean bdo = r.select(pt, pt.getTableName(), "", "index");
		while(bdo) {
			if(pt.id.equals(id))
				break;
			bdo = r.selectNext(pt);
			idx++;
		}
		r.close();
		return idx;
	}
}
