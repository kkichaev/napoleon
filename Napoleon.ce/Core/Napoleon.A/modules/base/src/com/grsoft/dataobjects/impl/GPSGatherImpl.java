package com.grsoft.dataobjects.impl;

import android.content.Context;
import com.grsoft.dataobjects.GPSGather;
import com.grsoft.napoleon.GPSGatherEdit;
import com.grsoft.napoleon.documents.CreatableDocument;


public class GPSGatherImpl extends CreatableDocument<GPSGather> {

	@Override
	public void open(Context context) {
		GPSGatherEdit.open(context, getRowid());
	}
	
	@Override
	public void postInit() {
		data.latitude = 0;
		data.longitude = 0;
	}

}
