package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.Features;
import com.grsoft.util.Consts;
import com.grsoft.util.FPOperation;

public class OrderImplEx extends OrderImpl {
	@Override
	public int weight() {
		int weight = 0;
		
		if( !Features.NO_WEIGHT_IN_ORDER && data.items != null ) {
			PriceImpl p = new PriceImpl();
			p.setReadingFields("weight");
			
			Price pd = p.getData();
			for (OrderItem item: data.items) {
				pd.id = item.id;
				
				if( ((OrderItemEx)item).inKG > 0  )
					weight += item.qty;
				else  if( p.read() )
					weight += FPOperation.itemMul(item.qty, pd.weight, Consts.QTY_SCALE);
			}
			p.close();
		}
		
		return weight;
	}
}
