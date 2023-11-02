package com.grsoft.dataobjects;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.grsoft.database.TableInfo;
import com.grsoft.util.Consts;

@TableInfo(name="Bonus", keyFields="created", indexes="order,def")
public class Bonus extends Order {
	public Date order;
	
	public void updateItems(List<OrderItem> src, HashMap<String, BonusDef> bonuses) {
		items.clear();

		for(OrderItem oi : src) {
			BonusDef bd = bonuses.get(oi.id);
			if( bd == null )
				continue;
			
			int qty = oi.qty / bd.qty;
			if( qty == 0 )
				continue;
			
			BonusItem item = new BonusItem();
			item.bonusid = bd.id;
			item.qty = qty * Consts.QTY_SCALE;
			item.id = oi.id;
			item.cost = 0;
			items.add(item);
		}
	}
}
