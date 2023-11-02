package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.Bonus;
import com.grsoft.napoleon.BonusProperties;
import com.grsoft.napoleon.OrderDetail;
import com.grsoft.napoleon.PriceCount;
import com.grsoft.napoleon.documents.BonusDoc;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;

public class BonusImpl extends OrderImplBase<Bonus> {

	@Override
	public CreatableDocument<Bonus> createInstance() {
		return new BonusImpl();
	}

	@Override
	public DocType getDocumentType() {
		return BonusDoc.instance();
	}

	@Override
	public void editProperties(Context ctx, boolean isOldOrder) {
		BonusProperties.open(ctx, this, isOldOrder);
	}

	@Override
	public void open(Context context) {
		OrderDetail.open(context, this);
	}

	@Override
	public void editItem(long itemRowid, Context context) {
		PriceCount.open(context, itemRowid, this); 
	}
}
