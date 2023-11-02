package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.grsoft.dataobjects.OrderItem;

public class OrderDetailEx extends OrderDetail {
	protected void setAdapter() {
		lvItems.setAdapter(new OrderItemsAdapter(){
			@Override
			protected void setItems(List<OrderItem> items) {
				HashSet<String> ids = new HashSet<String>();
				
				this.items = new ArrayList<OrderItem>();
				
				for(OrderItem item : items){
					if(!ids.contains(item.id)){
						this.items.add(item);
						ids.add(item.id);
					}else{
						OrderItem obj = findItem(item);
						
						if(obj != null)
							obj.qty += item.qty;
					}
				}
			}
			
			private OrderItem findItem(OrderItem item){
				for(OrderItem i: items)
					if(item.id.equals(i.id))
						return i;
				
				return null;
			}
		});
	};
}
