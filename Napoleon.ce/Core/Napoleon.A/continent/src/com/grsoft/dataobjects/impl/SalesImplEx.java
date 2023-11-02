package com.grsoft.dataobjects.impl;

import com.grsoft.napoleon.R;
import android.content.Context;

public class SalesImplEx extends SalesImpl {
	@Override
	protected boolean checkPriceQty() {	return false; }
	
	@Override
	public String getDescription(Context context) {
		StringBuilder sb = new StringBuilder();
		sb.append(data.number);
		
		String s = getPODRemark(context);
		if(s.length() > 0){
			sb.append("<br>");
			sb.append(s);
		}
		
		return sb.toString();
	}
	
	private String getPODRemark(Context context){
		return (data.podRemark.length() > 0) ? data.podRemark : 
			(isProceeded()) ?  context.getString(R.string.in_processeng) : 
			(isExported()) ? context.getString(R.string.sent) : 
			""; 
	}
}
