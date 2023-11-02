package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.OrderW;
import com.grsoft.napoleon.CreateOrder;
import com.grsoft.napoleon.OrderDeliveryDetail;
import com.grsoft.napoleon.OrderDetail;
import com.grsoft.napoleon.PriceCount;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.OrderWDoc;

public class OrderWImpl extends OrderImplBase<OrderW> implements Itemsable{
	@Override
	public void open(Context context) {
		if (data.number.length() == 0)
			OrderDetail.open(context, this);
		else
			OrderDeliveryDetail.open(context, this);
	}

	@Override
	public void editItem(long itemRowid, Context context ) {
		PriceCount.open(context, itemRowid, (DbObject<OrderW>)this); 
	}

	public void editProperties(Context ctx, boolean isOldOrder) {
		CreateOrder.open(ctx, this, isOldOrder);
	}

	@Override
	public CreatableDocument<OrderW> createInstance() { return new OrderWImpl(); }
	
	protected DocType getDocumentType() { return OrderWDoc.instance(); }
}
