package com.grsoft.dataobjects.impl;

import com.grsoft.napoleon.R;
import android.content.Context;


public class OrderImplEx extends OrderImpl {
	@Override
	public String getDescription(Context context) {
		StringBuilder result = new StringBuilder();
		
		if(data.number.length() > 0)
			result.append(data.number).append("<br>");
		
		result.append((data.podRemark.length() > 0) ? data.podRemark : 
			(isProceeded()) ?  context.getString(R.string.in_processeng) : 
			(isExported()) ? context.getString(R.string.sent) : 
			"");
		
		return result.toString();
	}
}
