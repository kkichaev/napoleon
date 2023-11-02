package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrderEx;

import android.content.Context;

public class OrderImplEx extends OrderImpl {
	@Override
	public String getDescription(Context context) {
		String ret = super.getDescription(context);
		OrderEx oe = (OrderEx)data;
		String add = "";
		if( oe.forwarder.length() > 0 ) {
			add += "Ёксп.:<i>" + oe.forwarder + "</i> ";
		}
		if( oe.phone.length() > 0 ) {
			add += "тел.:<font color='blue'><u>" + oe.phone + "</u></font>";
		}
		if( add.length() > 0 ) {
			ret += "<br>" + add;
		}
		return ret;
	}
}
