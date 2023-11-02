package com.grsoft.napoleon;

public class CreateReturnEx extends CreateReturn {
	@Override
	protected void initView() {
		findViewById(R.id.spPrices).setEnabled(false);
	}
}
