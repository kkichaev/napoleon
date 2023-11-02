package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.OrderProceededEx;

public class OrderImplEx extends OrderImpl {
	@Override
	public boolean isEditable() {
		return (!isExported() || 
				(isProceeded() && ((data.params & OrderProceededEx.APPROVED) ==  0)));
	}

	@Override
	public String getDescription(Context context) {
		if(data.number.length() > 0) {
			String ret = data.number;
			if(data.podRemark.length() > 0) {
				ret += "<br/>" + data.podRemark;
			}
			return ret;
		}
		return super.getDescription(context);
	}
}
