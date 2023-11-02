package com.grsoft.database;

public class PriceHitchingEx extends PriceHitching {
	@Override
	public void setPriceFilter(boolean rcvRemainPrice) {
		setCondition("folderID >= 0 and fid <> ''");
	}
}
