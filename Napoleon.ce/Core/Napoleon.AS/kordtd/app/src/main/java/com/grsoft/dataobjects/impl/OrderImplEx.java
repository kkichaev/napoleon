package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.PriceCount;
import com.grsoft.napoleon.PriceCountEx;
import com.grsoft.napoleon.PriceFreeCostCount;
import com.grsoft.napoleon.documents.CreatableDocument;

import android.content.Context;

public class OrderImplEx extends OrderImpl {
	@Override
	public void editItem(long itemRowid, Context context) {
		StringBuilder sb = new StringBuilder();
		ConfigImpl ci = new ConfigImpl();
		
		if( ci.getValue(sb, "—вободна€÷ена") && Integer.parseInt(sb.toString()) == 1) {
			PriceCount.activity = PriceFreeCostCount.class;
			Features.CAN_CHANGE_COST = true;
		} else {
			PriceCount.activity = PriceCountEx.class;
			Features.CAN_CHANGE_COST = false;
		}

		super.editItem(itemRowid, context);
	}
	
	@Override public CreatableDocument<Order> createInstance() { return new OrderImplEx(); }
}
