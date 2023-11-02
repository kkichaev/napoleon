package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.napoleon.ReturnDetailEx;
import com.grsoft.napoleon.ReturnPriceCount;
import com.grsoft.napoleon.ReturnProperties;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.ReturnDoc;

public class ReturnImplEx extends ReturnImpl {

	@Override public CreatableDocument<Return> createInstance() { return new ReturnImplEx(); }

	@Override
	public void editProperties(Context ctx, boolean isOldOrder) {
		ReturnProperties.open(ctx, this, isOldOrder);
	}
	
	@Override
	public DocType getDocumentType() { return ReturnDoc.instance(); }

	@Override
	public void open(Context context) {
		ReturnDetailEx.open(context, this);
	}

	@Override
	public void editItem(long itemRowid, Context context) {
		ReturnPriceCount.open(context, itemRowid, this);
	}

	@Override protected boolean checkPriceQty() { return false; }
	
	@Override
	public void postInit() {
		OrgImpl oi = new OrgImpl();
		OrgEx o = (OrgEx) oi.getData();
		o.id = data.id;
		oi.read();
		oi.close();
		
		data.supplyer = o.firm;
	}
}
