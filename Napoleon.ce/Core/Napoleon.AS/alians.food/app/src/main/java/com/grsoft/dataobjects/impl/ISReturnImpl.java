package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.ISReturn;
import com.grsoft.napoleon.ISReturnDetail;

public class ISReturnImpl extends DeliveryImplBase<ISReturn> {

	@Override
	public void open(Context context) {
		ISReturnDetail.open(context, this);
	}

	@Override
	public long sum() {
		return -data.sum();
	}
	
	@Override
	public String getDescription(Context context) {
		return data.number;
	}
}
