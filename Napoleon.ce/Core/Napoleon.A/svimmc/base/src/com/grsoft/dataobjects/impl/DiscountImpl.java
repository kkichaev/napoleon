package com.grsoft.dataobjects.impl;

import java.util.HashMap;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Discount;
import com.grsoft.dataobjects.DiscountItem;

public class DiscountImpl extends DbObject<Discount> {

	/**
	 * возвращает список скидок
	 * @return
	 */
	public static HashMap<String, Discount> load() {
		HashMap<String, Discount> ret = new HashMap<String, Discount>();
		
		String table = DataObjectInfo.getInstance().getTableName(Discount.class);
		DbReader r = new DbReader();
		Discount dd = new Discount();
		boolean bdo = r.select(dd, table, null);
		while( bdo ) {
			ret.put(dd.id, dd);
			dd = new Discount();
			bdo = r.selectNext(dd);
		}
		r.close();
		return ret;
	}

	/**
	 * Скидки по договору
	 * @param dogId
	 * @return
	 */
	public static HashMap<String, DiscountItem> loadFromDogovor(String dogId) {
		HashMap<String, DiscountItem> ret = new HashMap<String, DiscountItem>();
		
		DiscountImpl di = new DiscountImpl();
		Discount d = di.getData();
		d.id = dogId;
		if( di.read() ) {
			for(DiscountItem i : d.items)
				ret.put(i.id, i);
		}		
		di.close();
		
		return ret;
	}
}
