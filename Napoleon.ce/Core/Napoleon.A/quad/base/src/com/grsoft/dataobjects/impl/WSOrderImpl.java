package com.grsoft.dataobjects.impl;

import android.content.Context;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.WSOrder;
import com.grsoft.napoleon.CreateOrder;
import com.grsoft.napoleon.PriceCount;
import com.grsoft.napoleon.WSOrderDetail;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;

public class WSOrderImpl extends OrderImplBase<WSOrder> {
	@Override
	public void editItem(long itemRowid, Context context) {
		PriceCount.open(context, itemRowid, (DbObject<WSOrder>) this);
	}

	@Override
	public void editProperties(Context ctx, boolean isOldOrder) {
		CreateOrder.open(ctx, this, isOldOrder);
	}

	@Override
	public CreatableDocument<WSOrder> createInstance() {
		return new WSOrderImpl();
	}
	
	@Override public long sum() { return 0; }

	@Override
	public void open(Context context) {
		WSOrderDetail.open(context, this);
	}

	@Override
	public boolean init(Context context, String orgId, GpsCoord coord) {
		data.date = Util.getDate();
		data.created = Util.getDateTime();
		write();
		return true;
	}
	
	@Override
	public int getItemValue(Price item) {
		return ((PriceEx)item).whQty;
	}
}
