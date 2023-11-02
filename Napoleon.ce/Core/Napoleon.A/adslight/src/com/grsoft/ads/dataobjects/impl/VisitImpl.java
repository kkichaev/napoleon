package com.grsoft.ads.dataobjects.impl;

import com.grsoft.ads.dataobjects.VisitEx;
import com.grsoft.dataobjects.VisitItem;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.Util;

import android.content.Context;


public class VisitImpl extends CreatableDocument<VisitEx> {

	@Override
	public void postInit() {
		super.postInit();
		data.date = Util.getDateTime();
	}
	
	@Override
	public void open(Context context) { }

	public void addPhoto(String path) {
		VisitItem item = new VisitItem();
		item.id = path.getBytes();
		data.items.add(item);
	}
}
