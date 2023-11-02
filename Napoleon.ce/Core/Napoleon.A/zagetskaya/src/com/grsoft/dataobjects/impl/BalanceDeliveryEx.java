package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.napoleon.documents.BalanceDelivery;
import android.content.Context;

public class BalanceDeliveryEx extends BalanceDelivery {
	
	@Override
	public String getDescription(Context context) {
		StringBuilder sb = new StringBuilder();
		sb.append(super.getDescription(context));
	
		String a = ((DeliveryEx)data).agent.trim();;
		
		if(a.length() > 0){
			sb.append("<br>");
			sb.append(a);
		}
			
		
		return sb.toString();
	}
}
