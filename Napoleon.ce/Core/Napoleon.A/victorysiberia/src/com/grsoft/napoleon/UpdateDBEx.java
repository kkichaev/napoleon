package com.grsoft.napoleon;


public class UpdateDBEx extends UpdateDB {
	@Override
	protected void postSync(Boolean result) {
		if(result){
			CostStrategyEx.refreshCash();
		}
	}
}
