package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.napoleon.Features;
import com.grsoft.util.Consts;
import com.grsoft.util.FPOperation;

public class OrderImplEx extends OrderImpl {
	
	/***
	 * литр
	 */
	@Override
	public int weight() {
		int weight = 0;
		
		if( !Features.NO_WEIGHT_IN_ORDER && data.items != null ) {
			PriceImpl p = new PriceImpl();
			p.setReadingFields("tank");
			
			PriceEx pd = (PriceEx) p.getData();
			for (OrderItem item: data.items) {
				pd.id = item.id;
				
				if( p.read() )
					weight += FPOperation.itemMul(item.qty, pd.tank, Consts.QTY_SCALE);
			}
			p.close();
		}
		
		return weight;
	}
}
