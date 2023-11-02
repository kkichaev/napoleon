package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceQty;
import com.grsoft.util.view.dialog_helper.DialogHelper;

public class OrderImplEx extends OrderImpl {

	int whCount = 0;
	int qtyIndex = 0;
	HashMap<String, Integer> qty = new HashMap<String, Integer>();
	
	public int getWhIndex() {
		if( whCount == 0 ) {
			ConfigImpl ci = new ConfigImpl();
			Config c = ci.getData();
			c.key = "Склады";
			if(ci.read()) {
				List<CharSequence> values = new ArrayList<CharSequence>();
				DialogHelper.makeList(c.value, values);
				whCount = values.size();
			}
			ci.close();
		}
		
		OrderEx oe = (OrderEx)data;
		
		int wh = oe.whIndex;
//		int fi = oe.supplyer;
//		return wh + fi * whCount;
		return wh;
	}
	
	@Override
	public int getItemValue(Price item) {
		int index = getWhIndex();
		if( qtyIndex != index || qty.size() == 0 ) {
			qty.clear();
			PriceQty pc = new PriceQty();
			String table = DataObjectInfo.getInstance().getTableName(pc.getClass());
			DbReader r = new DbReader();
			boolean bdo = r.select(pc, table, "type=" + Integer.toString(index));
			while(bdo) {
				qty.put(pc.id, pc.qty);
				bdo = r.selectNext(pc);
			}
			r.close();
			this.qtyIndex = index;
		}
		Integer val = qty.get(item.id);
		return (val == null) ? 0 : val;
	}
}
