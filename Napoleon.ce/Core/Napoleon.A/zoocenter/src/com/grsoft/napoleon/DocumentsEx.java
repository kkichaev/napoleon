package com.grsoft.napoleon;

import android.os.Bundle;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;

public class DocumentsEx extends Documents {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	protected String orgInfo(Org o) {
		StringBuilder sb = new StringBuilder();
		sb.append("<i>").append(o.name).append("</i>");
		
		if(o.address.trim().length() > 0)
			sb.append("<br>").append(o.address);
			
		OrgEx oe = (OrgEx)o; 
		if(oe.workTime.trim().length() > 0)	
			sb.append("<br>").append(getString(R.string.work_time)).append(" ").append(oe.workTime);
		
		return sb.toString();
	}
}
