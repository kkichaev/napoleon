package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.ReturnCountEx;

import android.content.Context;

public class ReturnImplEx extends ReturnImpl {
	@Override
	public void editItem(long itemRowid, Context context) {
		ReturnCountEx.open(context, itemRowid, this);
	}
	
	@Override
	public void postInit() {
		OrgImpl oi = new OrgImpl();
		OrgEx o = (OrgEx)oi.getData();
		o.id = data.id;
		oi.read();
		oi.close();
		
		data.prcType = o.priceType;
		
		super.postInit();
	}
}
