package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;

public class PriceCountEx extends PriceCount {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View trQty = findViewById(R.id.trQty);
		if( trQty != null )
			trQty.setVisibility(View.GONE);
	}
}
