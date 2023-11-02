package com.grsoft.napoleon;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.Consts;

public class OrderDetailEx extends OrderDetail {
	@Override
	protected void updateTotalSum() {
		updateTotalSum(doc.sum(), doc.weight(), countItems());
	}

	private int countItems() {
		if( !(doc.getData() instanceof Order) )
			return 0;
		
		int count = 0;
		PriceImpl pi = new PriceImpl();
		PriceEx pe = (PriceEx) pi.getData();
		for(OrderItem i : doc.getData().items ) {
			pe.id = i.id;
			if(pi.read()) {
				if( pe.isWeight == 0 )
					count += i.qty;
			}
		}
		
		pi.close();
		
		return count / Consts.QTY_SCALE;
	}
}
