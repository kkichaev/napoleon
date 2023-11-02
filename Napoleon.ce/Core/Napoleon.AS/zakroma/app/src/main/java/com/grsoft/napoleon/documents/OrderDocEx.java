package com.grsoft.napoleon.documents;

import java.util.HashMap;
import java.util.Map;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.R;
import android.content.Context;

public class OrderDocEx extends OrderDoc{
	
	public static void init() { instance = new OrderDocEx(); }
	
	protected OrderDocEx() { super("Заявки", "Order", OrderImplEx.class);} 
	
	private DeliveryImpl delivery = new DeliveryImpl();
	
	@Override
	public int getViewTextColor(Context context, Document<?> doc) {
		Map<String, Integer> dlvvals = new HashMap<String, Integer>();
		
		Order order = (Order) doc.getData();
		delivery.getData().id = order.id;
		delivery.getData().number = order.number;
		
		if(delivery.read()){
			dlvvals.clear();
			
			for(DeliveryItem i : delivery.getData().items)
				dlvvals.put(i.id, i.qty);
		}
		
		delivery.close();
		
		int color = super.getViewTextColor(context, doc);
		
		if(dlvvals.size() > 0){
			for(OrderItem i : order.items)
				if(!dlvvals.containsKey(i.id) || dlvvals.get(i.id) != i.qty){
					color = context.getResources().getColor(R.color.red);
					break;
				}
		}
		
		return color; 
	}
}
