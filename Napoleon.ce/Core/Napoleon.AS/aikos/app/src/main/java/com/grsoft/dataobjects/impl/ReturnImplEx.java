package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.Return;
import com.grsoft.napoleon.ReturnPriceCount;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.ReturnDoc;

public class ReturnImplEx extends ReturnImpl {

	@Override public CreatableDocument<Return> createInstance() { return new ReturnImplEx(); }

	@Override
	public DocType getDocumentType() { return ReturnDoc.instance(); }

	@Override
	public void editItem(long itemRowid, Context context) {
		ReturnPriceCount.open(context, itemRowid, this);
	}

	@Override protected boolean checkPriceQty() { return false; }
}
