package com.grsoft.napoleon;

import java.util.HashMap;
import java.util.List;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.impl.OrderImplBase;

public class OrderChecker {
	
	HashMap<String, OrderItemEx> items = new HashMap<String, OrderItemEx>(); 
	
	public void set(Order o) {
		items.clear();
		if( o.items != null ) {
			for(OrderItem oi : o.items) {
				OrderItemEx oe = new OrderItemEx((OrderItemEx)oi);
				items.put(oi.id, oe);
			}
		}
	}
	
	public boolean isChanged(Order o) {
		HashMap<String, OrderItemEx> di = new HashMap<String, OrderItemEx>(items);
		
		for(OrderItem oi : o.items) {
			OrderItemEx val = di.get(oi.id);
			if( val == null || val.qty != oi.qty )
				return true;
			di.remove(oi.id);
		}		
		return (di.size() != 0);
	}
	
	public boolean isChanged(OrderItemEx item) {
		OrderItemEx val = items.get(item.id);
		return val == null ? true : (val.qty != item.qty || !val.taxType.equals(item.taxType));		
	}
	
//	public Integer get(String id) {
//		OrderItemEx val = items.get(id);
//		return val == null ? null : val.qty;
//	}

	/**
	 * Восстанавливаем заявку, но не откатываем назад остатки.
	 * @param doc
	 */
	public void restoreOrder(OrderImplBase<? extends Order> doc) {

		List<OrderItem> docItems = doc.getData().items;
		docItems.clear();		
		docItems.addAll(items.values());
		doc.write();
		
		doc.getDocumentType().refreshDocSum(doc.getId());
	}

	public void clear() {
		items.clear();
	}

	public boolean haveData() {
		return items.size() > 0;
	}
}
