package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.Movement;
import com.grsoft.napoleon.OrderDetail;
import com.grsoft.napoleon.PriceCount;
import com.grsoft.napoleon.Warehouse;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.MovementDoc;

public class MovementImpl extends OrderImplBase<Movement>{

	@Override
	public void editItem(long itemRowid, Context context) {
		PriceCount.open(context, itemRowid, (DbObject<Movement>)this);
	}

	@Override
	public void editProperties(Context ctx, boolean isOldOrder) {
		Warehouse.open(ctx, this, isOldOrder);
	}

	@Override
	public CreatableDocument<Movement> createInstance() {
		return new MovementImpl();
	}

	@Override
	public void open(Context context) {
		OrderDetail.open(context, this);
	}
	
	public DocType getDocumentType() { return MovementDoc.instance(); }
}
