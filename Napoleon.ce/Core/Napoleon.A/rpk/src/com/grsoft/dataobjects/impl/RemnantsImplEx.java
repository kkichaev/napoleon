package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.MtxRemnants;

import android.content.Context;

public class RemnantsImplEx extends RemnantsImpl {
	@Override
	protected void openPrice(Context context) {
		if(!openMtxRemnants(context))
			super.openPrice(context);
	}

	protected boolean openMtxRemnants(Context context) {
		boolean result = false;
		OrgImpl orgImpl = new OrgImpl();
		orgImpl.getData().id = getId();
		
		if(orgImpl.read()){
			OrgEx org = (OrgEx) orgImpl.getData();
			
			if(org.remnants != null && org.remnants.size() > 0){
				MtxRemnants.open(context, getRowid());
				result = true;
			}
		}
		
		orgImpl.close();
		
		return result;
	}
}
