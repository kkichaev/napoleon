package com.grsoft.napoleon;

public class PriceCountEx extends PriceCount {
	@Override
	protected void refreshData() {
		super.refreshData();
		cbPackets.setEnabled(false);
	}
}
