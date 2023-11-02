package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.OrderImpl;

import android.content.Context;

public class NapoleonAppNew extends NapoleonApp {
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		OrderImpl.OrderEditor = new OrderEditor();
	}
	
	@Override
	protected void initChildActivity() {
		super.initChildActivity();
		Warehouse.activity = WarehouseEx2.class;
	}
}
