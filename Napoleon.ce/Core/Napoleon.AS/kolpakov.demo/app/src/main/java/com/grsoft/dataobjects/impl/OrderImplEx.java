package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.napoleon.OrderTabakDetail;
import com.grsoft.util.ExtrasConst;

import android.content.Context;

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
		if(((OrderEx)data).tabak != 0) {
			OrderTabakDetail.open(context, this);
		} else {
			super.open(context);
		}
	}

	public boolean isGood() {
		if(!isEditable()) return true;
		if(((OrderEx)data).tabak == 1) {
			for(OrderItem oid : data.items) {
				if(!((OrderItemEx)oid).isScanned())
					return false;
			}
		}
		return true;
	}
}
