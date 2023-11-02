package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.ReturnCount;
import com.grsoft.napoleon.util.DeliveryList;

import android.content.Context;

public class ReturnImplEx extends ReturnImpl {
	@Override
	public void editItem(long itemRowid, Context context) {
		ReturnCount.open(context, itemRowid, this);
	}
	
	@Override
	public int getItemValue(Price item) {
		DeliveryList dl = DeliveryList.open(data.id);
		return dl.getItemQty(null, null, item.id);
	}
}
