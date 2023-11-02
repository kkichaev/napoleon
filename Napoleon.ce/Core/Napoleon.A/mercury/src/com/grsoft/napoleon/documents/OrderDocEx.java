package com.grsoft.napoleon.documents;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.impl.OrderImplBase;

public class OrderDocEx extends OrderDoc {
	public static void init() {
		instance = new OrderDocEx();
	}
	
	@Override
	public void getItemsFromLastDoc(String id, List<String> itemIds, int period) {
		DocList list = docList(id, "created DESC");
		if( list.getCount() > 1 ) {
			int index = 1;
			Calendar c = Calendar.getInstance();
			//c.add(Calendar.MONTH, -1);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.DAY_OF_MONTH, 1);
			Date checkDate = c.getTime();
			
			for( ; index < list.getCount(); index++ ) {
				@SuppressWarnings("unchecked")
				OrderImplBase<? extends Order> doc = (OrderImplBase<? extends Order>)list.get(index);
				if( doc == null || doc.getData().created.before(checkDate) )
					break;
				
				addItemsId(itemIds, doc);
			}
		}
		list.close();
	}
}
