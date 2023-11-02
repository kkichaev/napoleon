package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.RemnantItem;
import com.grsoft.dataobjects.Remnants;
import com.grsoft.manager.RemnantsDetail;
import com.grsoft.napoleon.documents.CreatableDocument;


public class MRemnantsImpl extends CreatableDocument<Remnants> {

	@Override
	public void open(Context context) { RemnantsDetail.open(context, this);	}
	
	@Override public long sum() { return 0; }
	
	@Override
	public int qty() {
		int sm = 0;
		if( data.items != null )
			for(RemnantItem si : data.items) {
				sm += ((RemnantItem)si).qty;
		}
		return sm;
	}
}
