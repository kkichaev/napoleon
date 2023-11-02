package com.grsoft.napoleon;

import com.grsoft.dataobjects.PriceEx;

public class PriceCountEx extends PriceCount {
	@Override
	protected void refreshData() {
		super.refreshData();
		
		//cbPackets.setEnabled(((PriceEx)price.getData()).boxed == 0);
	}
	
	@Override
	protected boolean getStartInPack() {
		return ((PriceEx)price.getData()).boxed != 0;
	}
}
