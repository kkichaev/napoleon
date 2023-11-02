package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.OrderImplEx;

import android.os.Bundle;
import android.view.View;

public class PriceCountEx extends PriceCount {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViewById(R.id.cbPackets).setVisibility(View.INVISIBLE);
	}
	
	@Override
	protected boolean getStartInPack() {
		return (document instanceof OrderImplEx) ? true : super.getStartInPack();
	}
}
