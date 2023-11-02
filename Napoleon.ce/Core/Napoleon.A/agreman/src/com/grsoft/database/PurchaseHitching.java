package com.grsoft.database;

import com.grsoft.dataobjects.Purchase;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.documents.PurchaseDoc;

public class PurchaseHitching extends RcvNewHitching {

	public PurchaseHitching() {
		super(DbObject.getDataType(Purchase.class), PurchaseDoc.DOC_NAME);
	}

}
