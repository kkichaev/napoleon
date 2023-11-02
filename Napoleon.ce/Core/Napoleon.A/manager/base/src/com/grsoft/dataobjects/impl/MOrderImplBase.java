package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.manager.OrderDetail;
import com.grsoft.napoleon.documents.CreatableDocument;

public class MOrderImplBase<T extends Order> extends CreatableDocument<T> {

	@Override
	public void open(Context context) {	OrderDetail.open(context, this); }

	public int qty() {
		int result = 0;
		
		if( data.items != null ) {
			for(OrderItem item : data.items)
				result += item.qty;
		}
		
		return result;
	}
}
