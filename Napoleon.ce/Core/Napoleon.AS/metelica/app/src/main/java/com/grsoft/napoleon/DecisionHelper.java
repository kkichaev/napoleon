package com.grsoft.napoleon;

import android.content.Context;


public class DecisionHelper {
	public static String getDecisionText(Context context, int code){
		String result = "";
		
		switch(code){
		case 0:
			result = context.getString(R.string.approved);
			break;
		case 1:
			result = context.getString(R.string.rejected);
			break;
		case 2:
			result = context.getString(R.string.toedit);
			break;
		default:
			result = String.format("код <%d> не найден", code);
		}
		
		return result;
	}
}
