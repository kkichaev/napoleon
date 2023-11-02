package com.grsoft.dataobjects.impl;

import java.util.Calendar;

import com.grsoft.napoleon.ReturnPriceCount;
import com.grsoft.util.Util;

import android.content.Context;

public class ReturnImplEx extends ReturnImpl {
	@Override
	public void editItem(long itemRowid, Context context) {
		ReturnPriceCount.open(context, itemRowid, this);
	}
	
	@Override
	public void postInit() {
		super.postInit();
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(Util.getDate());
		cal.add(Calendar.DATE, 1);
		
		data.date = cal.getTime();
		 
		
	}
}
