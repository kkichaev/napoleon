package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.Org;
import com.grsoft.napoleon.ReturnProperties;

public class ReturnImplEx extends ReturnImpl {
	@Override
	public void editProperties(Context context, boolean isOldOrder) {
		ReturnProperties.open(context, this, isOldOrder);
	}
	
	@Override
	public void postInit() {
		OrgImpl oi = new OrgImpl();
		Org o = oi.getData();
		o.id = data.id;
		oi.read();
		oi.close();
		
		data.sumType = o.costype;
	}
}
