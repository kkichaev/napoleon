package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.ArchIncass;
import com.grsoft.napoleon.ArchIncassEdit;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.GpsCoord;

public class ArchIncassImpl extends CreatableDocument<ArchIncass> {

	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		return false;
	}
	
	@Override
	public void open(Context context) {
		ArchIncassEdit.open(context, this);
	}
	
	@Override
	public boolean delete() {
		return true;
	}
	
	@Override
	public void deleteAll() {
	}
	
	@Override
	public long sum() {
		return data.sum;
	}

}
