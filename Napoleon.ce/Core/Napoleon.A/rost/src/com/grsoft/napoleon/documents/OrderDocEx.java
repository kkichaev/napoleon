package com.grsoft.napoleon.documents;

import android.view.View;
import android.widget.Adapter;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.OrderHelper;


public class OrderDocEx extends OrderDoc {
	public static void init() { instance = new OrderDocEx(); }
	
	private OrderDocEx() {
		super("Заявки", "Order", OrderImplEx.class);
	}
	
	@Override
	public void setView(Adapter adapter, View view, Document<?> doc) {
		super.setView(adapter, view, doc);
		OrderHelper.setDriverView(view, (OrderEx)doc.getData());
	}

}
