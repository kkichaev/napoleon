package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.Stock;
import com.grsoft.napoleon.documents.Document;

public class StockImpl extends Document<Stock>{
	
	public StockImpl(){
		super();
	}
	
	public StockImpl(DeliveryItem item, String name) {
		super();
		
		data.name = name;
		data.id = item.id;
		data.qty = item.qty;
	}
	
	@Override public String getDescription(Context context) { return data.name; }
	@Override public long sum() { return data.qty; }

	@Override
	public void open(Context context) {}
}
