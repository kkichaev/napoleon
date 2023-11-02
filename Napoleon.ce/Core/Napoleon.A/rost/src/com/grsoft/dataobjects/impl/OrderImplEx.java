package com.grsoft.dataobjects.impl;

import android.content.Context;


public class OrderImplEx extends OrderImpl{
	@Override
	public String getDescription(Context context) {
		StringBuilder result = new StringBuilder();
		result.append(data.number.trim());
		
		if (result.length() > 0)
			result.append("<\br>");
		
		
		
		return super.getDescription(context);
	}
}
