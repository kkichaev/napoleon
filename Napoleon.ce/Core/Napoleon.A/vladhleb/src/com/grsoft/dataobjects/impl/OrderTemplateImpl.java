package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderTemplate;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Util;

public class OrderTemplateImpl extends OrderImplBase<OrderTemplate> {

	@Override
	public void editItem(long itemRowid, Context context) {
	}

	@Override
	public void editProperties(Context ctx, boolean isOldOrder) {
	}

	@Override public CreatableDocument<OrderTemplate> createInstance() { return new OrderTemplateImpl(); }

	@Override
	public void open(Context context) {
	}

	public OrderImpl makeOrder() {
		
		OrderImpl oi = (OrderImpl) OrderDoc.instance().create();
		Order o = oi.getData();
		DbReader r = new DbReader();
		r.read(o, getTableName(), getRowid());
		r.close();
		
		o.created = Util.getDateTime();
		o.date = Util.getDate();

		oi.write();
		oi.close();
		return oi;
	}
}
