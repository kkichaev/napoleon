package com.grsoft.dataobjects.impl;

import com.grsoft.napoleon.OrderDetailRcvd;

import android.content.Context;

public class OrderImplEx extends OrderImpl {
	@Override
	public void open(Context context) {
		if (data.number.length() != 0 || !OrderReceivedImpl.haveData(data))
			super.open(context);
		else
			OrderDetailRcvd.open(context, this);
	}
}
