package com.grsoft.dataobjects.impl;

import java.util.HashMap;
import java.util.Map.Entry;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;

public class OrderImplEx extends OrderImpl {
	public void auto() {
		class AvgData{
			int qty;
			int count;
		}
		
		HashMap<String, AvgData> items = new HashMap<String,AvgData>();

		DocList dl = OrderDoc.instance().docList(getData().id, "created DESC", "rowid != " + getRowid());
		int count = dl.getCount();
		final int MAX_HISTORY_ORDER_LEN = 3;
		
		if(count > MAX_HISTORY_ORDER_LEN)
			count = MAX_HISTORY_ORDER_LEN;
		
		for (int i = 0; i < count; i++) {
			OrderImplBase<?> doc = (OrderImplBase<?>) dl.get(i);
			for (OrderItem item : doc.data.items) {
				AvgData qty = items.get(item.id);
				if (qty == null){
					qty = new AvgData();
					qty.qty = item.qty;
					qty.count = 1;
				}else{
					qty.qty += item.qty;
					qty.count += 1;
				}
				items.put(item.id, qty);
			}
		}
		dl.close();
		
		HashMap<String, Integer> retItems = new HashMap<String, Integer>();
		
		for(Entry<String, AvgData> se : items.entrySet()) {
			AvgData qty = se.getValue();
			int val = qty.qty / qty.count;
			
			int rem = val % Consts.QTY_SCALE;
			
			if(rem > 0){
				int ost = val / Consts.QTY_SCALE;
				
				if (rem >= 5 * Consts.QTY_SCALE / 10)
					val = (ost + 1) * Consts.QTY_SCALE;
				else
					val = ost * Consts.QTY_SCALE;
			}
			
			retItems.put(se.getKey(), val);
		}

		autoorder(getData().id, null, retItems, true);
	}
}
