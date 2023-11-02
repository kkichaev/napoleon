package com.grsoft.napoleon;

public class UpdateDB2Ex extends UpdateDBEx {
	@Override
	protected void postSync(Boolean result) {
		super.postSync(result);
		CostStrategyEx.resetCash();
	}

}
