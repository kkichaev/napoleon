package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.util.view.dialog_helper.DialogHelper;

public class OutOrderHelper {
	public static boolean needExplain() {
		boolean ret = false;
		ConfigImpl ci = new ConfigImpl();
		StringBuilder sb = new StringBuilder();
		
		if(ci.getValue(sb, "Объяснительная") && sb.length() > 0) {
			ret = (Integer.parseInt(sb.toString()) > 0); 
		}
		return ret;
	}
	
	public static CharSequence[] outOrderCause() {
		List<CharSequence> ret = new ArrayList<CharSequence>();
		
		ConfigImpl ci = new ConfigImpl();
		StringBuilder sb = new StringBuilder();
		if( ci.getValue(sb, "ПричиныНедозаказа")) {
			DialogHelper.makeList(sb.toString(), ret, "");
		}
		
		return ret.toArray(new CharSequence[] {});
	}
}
