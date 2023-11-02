package com.grsoft.napoleon;

import android.view.View;

public class DeliveryDetailEx extends DeliveryDetail {
	@Override
	public void updateTotalSum(int sum, int weight, int count) {
		View v = findViewById(R.id.tvTotalSum);
		if( v != null )
			v.setVisibility(View.GONE);
	}
}
