package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;

public class PriceCount2Ex extends PriceCountEx {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		View trSum = findViewById(R.id.trSum);
		
		if(trSum != null)
			trSum.setVisibility(View.GONE);
		
		View trCost = findViewById(R.id.trCost);
		
		if(trCost != null)
			trCost.setVisibility(View.GONE);
	}
}
