package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderPending;
import com.grsoft.util.FPOperation;


public class OrderPendingImpl extends MOrderImplBase<OrderPending> {
	@Override
	public long sum() {
		long result = 0;
		if( data.items != null ) {
			DataObjectInfo info = DataObjectInfo.getInstance();
			int qty_scale = info.getScale(OrderItem.class, "qty");
			
			for (OrderItem orderItem: data.items)
				result += FPOperation.itemMul(orderItem.cost, orderItem.qty, qty_scale);
		}
		
		return result;
	}
}
