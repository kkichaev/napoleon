package com.grsoft.napoleon;

import android.content.Context;

import com.grsoft.dataobjects.impl.SalesImpl;

public class SalesPropertiesEditor {
	public void edit(Context ctx, SalesImpl doc, boolean isOldOrder) {
		CreateSales.open(ctx, doc.getRowid(), isOldOrder);		
	}
}
