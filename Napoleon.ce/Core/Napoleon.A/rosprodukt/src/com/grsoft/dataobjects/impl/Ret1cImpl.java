package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.Ret1c;
import com.grsoft.napoleon.Ret1cDetail;

import android.content.Context;

public class Ret1cImpl extends DeliveryImplBase<Ret1c> {
	@Override public void open(Context context) { Ret1cDetail.open(context, this); }
}
