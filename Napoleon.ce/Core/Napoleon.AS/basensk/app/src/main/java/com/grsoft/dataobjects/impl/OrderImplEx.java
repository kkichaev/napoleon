package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Pair;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class OrderImplEx extends OrderImpl {
	@Override
	public int getItemValue(Price item) {
		PriceEx pe = (PriceEx)item;
		int whIndex = ((OrderEx)data).whIndex;
		
		if( whIndex <= 0 || whIndex > pe.whQty.size())
			return super.getItemValue(item);
		
		return pe.whQty.get(whIndex-1).qty;
	}

	public long insert() {
		rowid = ExtrasConst.INVALID_ROWID;
		write();
		close();
		return rowid;
	}

	@Override
	public void open(Context context) {
		super.open(context);
	}

	public List<Pair<PriceEx, OrderItemEx>> makeIntersect(List<PriceEx> src) {
		List<Pair<PriceEx, OrderItemEx>> ret = new ArrayList<>();

		for(PriceEx p : src) {
			for(OrderItem oi : data.items) {
				if(oi.id.equals(p.id))
					ret.add(new Pair(p, oi));
			}
		}

		return ret;
	}
}
