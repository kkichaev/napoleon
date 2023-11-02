package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.Rko;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.RkoInfo;


public class RkoImpl extends PkoImplBase<Rko>{

	@Override
	public void open(Context context) {	RkoInfo.open(context, rowid); }

	@Override
	public String getDescription(Context context) {
		return (data.podRemark.length() > 0) ? data.podRemark : 
				(isProceeded()) ?  context.getString(R.string.in_processeng) : 
				(isExported()) ? context.getString(R.string.sent) : 
				""; 
	}
}
