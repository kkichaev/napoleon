package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.PlanRoute;
import com.grsoft.napoleon.documents.DocumentUtils;

public class PlanRouteImpl extends DbObject<PlanRoute> {
	public void setExported(boolean value){
		DocumentUtils.setExported(this, data.params, value);
	}	
	
	public boolean isExported(){
		return DocumentUtils.isExported(data.params); 
	}
	
	public boolean isApproved(){
		boolean result = false;
		PlanApproveImpl appr = new PlanApproveImpl();
		appr.data.plan = data.plan;
		
		if(appr.read()){
			result = true;
		}
		
		appr.close();
		
		return result;
		
	}
}
