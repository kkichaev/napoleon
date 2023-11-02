package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PricePrint;
import com.grsoft.dataobjects.WSOrder;
import com.grsoft.napoleon.PriceCount;
import com.grsoft.napoleon.WSOrderDetail;
import com.grsoft.napoleon.WSOrderEdit;
import com.grsoft.napoleon.Warehouse;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.util.GpsCoord;

import android.content.Context;

public class WSOrderImpl extends OrderImplBase<WSOrder> {
	@Override
	public void editItem(long itemRowid, Context context) {
		PriceCount.open(context, itemRowid, (DbObject<WSOrder>) this);
	}

	@Override
	public void editProperties(Context ctx, boolean isOldOrder) {
		WSOrderEdit.open(ctx, getRowid(), true);
	}

	@Override
	public CreatableDocument<WSOrder> createInstance() {
		return new WSOrderImpl();
	}
	
	@Override
	public void open(Context context) {
		WSOrderDetail.open(context, this);
	}
	
	@Override
	public int getItemValue(Price item) {
		return ((PricePrint)item).vanQty;
	}

	@Override
	public boolean init(Context context, String orgId, GpsCoord coord) {
		super.init(context, orgId, coord);

		DocType.setCurDoc(WSOrderDoc.instance());
		WSOrderEdit.open(context, getRowid(), false);

		return false;
	}
	
	public int getItemCentrValue(Price item) {
		return super.getItemValue(item);
	}
}
