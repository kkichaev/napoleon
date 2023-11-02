package com.grsoft.dataobjects.impl;

import android.content.Context;
import com.grsoft.dataobjects.ArchReturn;
import com.grsoft.napoleon.ArchCreateReturn;
import com.grsoft.napoleon.ReturnDetail;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.GpsCoord;


public class ArchReturnImpl extends OrderImplBase<ArchReturn>{

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
	public void editProperties(Context ctx, boolean isOldOrder) { ArchCreateReturn.open(ctx, this, isOldOrder);}


	@Override
	public CreatableDocument<ArchReturn> createInstance() {	return new ArchReturnImpl(); }


	@Override
	public void open(Context context) { ReturnDetail.open(context, this); }
	
	@Override
	public long sum() { return -super.sum(); }
}
