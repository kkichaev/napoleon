package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.AgentPrefixEx;
import com.grsoft.napoleon.R;

import android.content.Context;

public class OrderImplEx extends OrderImpl {
	@Override
	public String getDescription(Context context) {
		String podRemark = (data.podRemark.length() > 0) ? data.podRemark : 
			(isProceeded()) ?  context.getString(R.string.in_processeng) : 
			(isExported()) ? context.getString(R.string.sent) : 
			""; 
		return (data.number.length() > 0) ? 
				data.number + "<br>" + podRemark: 
				podRemark;
				 
	}
	
	@Override
	public void postInit() {
		super.postInit();

		AgentPrefixEx ae = (AgentPrefixEx) AgentPrefix.get();
		if( ae != null )
			data.supplyer = ae.firma;
	}
}
