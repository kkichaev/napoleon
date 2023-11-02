package com.grsoft.dataobjects.impl;

import android.content.Context;
import com.grsoft.dataobjects.ArchSales;
import com.grsoft.napoleon.ArchCreateSales;
import com.grsoft.napoleon.ArchSalesDetail;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.GpsCoord;


public class ArchSalesImpl extends OrderImplBase<ArchSales>{
	
	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		return false;
	}
	
	
	@Override
	public boolean delete() {
		return true;
	}
	
	@Override
	public void deleteAll() {
	}

	@Override
	public boolean isEditable() { return false;	}


	@Override
	public void editItem(long itemRowid, Context context) {}


	@Override
	public void editProperties(Context ctx, boolean isOldOrder) { ArchCreateSales.open(ctx, this, isOldOrder);}


	@Override
	public CreatableDocument<ArchSales> createInstance() {	return new ArchSalesImpl(); }


	@Override
	public void open(Context context) { ArchSalesDetail.open(context, this); }
}
