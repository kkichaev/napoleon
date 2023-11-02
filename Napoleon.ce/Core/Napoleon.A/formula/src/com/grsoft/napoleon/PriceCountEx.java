package com.grsoft.napoleon;

import android.os.Bundle;

import com.grsoft.dataobjects.PriceEx;

public class PriceCountEx extends PriceCount {
	@Override
	protected boolean getStartInPack() {
		return ((PriceEx)price.getData()).pack != 0;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cbPackets.setEnabled(((PriceEx)price.getData()).pack == 0);
	}
}
