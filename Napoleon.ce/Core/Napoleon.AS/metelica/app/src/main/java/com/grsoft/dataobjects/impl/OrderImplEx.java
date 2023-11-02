package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrderDecision;
import com.grsoft.napoleon.DecisionHelper;

import android.content.Context;


public class OrderImplEx extends OrderImpl {
	
	@Override
	public String getDescription(Context context) {
		return (data.number.length() > 0) ? 
				data.number : getDescritionW(context);
	}
	
	public String getDescritionW(Context context){
		String result = data.podRemark ;
		
		if(result.length() == 0){
			OrderDecision decision = OrderDecisionImpl.getDecision(data.created);
			if(decision != null)
				result = DecisionHelper.getDecisionText(context, decision.decision);
			else
				result = super.getDescription(context);
		}
		
		return result;
	}
	
	@Override
	public boolean isProceeded() {
		return OrderDecisionImpl.getDecision(data.created) != null ||  super.isProceeded();
	}
}

