package com.grsoft.napoleon;

import android.content.Context;

import com.grsoft.dataobjects.impl.SalesBaseImpl;
import com.grsoft.dataobjects.impl.SalesImpl;

public class SalesPropertiesEditor {
	public void edit(Context ctx, SalesBaseImpl<?> doc, boolean isOldOrder) {
		CreateSales.open(ctx, doc.getRowid(), isOldOrder);		
	}
}
