package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.napoleon.ReturnPriceCount;
import com.grsoft.util.Consts;
import com.grsoft.util.GpsCoord;

public class ReturnImplEx extends ReturnImpl {
	public static Order order;
		
	@Override
	public void editItem(final long itemRowid, Context context) {
		ReturnPriceCount.open(context, itemRowid, this);
	}

	@Override
	public void postInit() {
		super.postInit();

		if (order != null) {
			((ReturnEx) data).ordcrt = order.created;
			order = null;
		}
	}

	public long newSum(String item, long itemSum) {
		long sum = itemSum;
		if(order != null) {
			for(OrderItem oi : order.items) {
				if(oi.id.equals(item)) {
					continue;
				} else {
					sum += ((long)oi.cost * oi.qty / Consts.QTY_SCALE);
				}
			}
		}
		return sum;
	}
}
