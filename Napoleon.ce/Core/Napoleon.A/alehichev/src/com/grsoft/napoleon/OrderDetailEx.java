package com.grsoft.napoleon;

import android.view.View;

import com.grsoft.dataobjects.OrderItem;

public class OrderDetailEx extends OrderDetail {
	@Override
	protected void setAdapter() {
		lvItems.setAdapter(new Adapter());
	}
	
	class Adapter extends OrderItemsAdapter {
		@Override
		protected void drawInternal(View view, String name, int color, OrderItem item) {
			super.drawInternal(view, name, color, item);
			view.findViewById(R.id.tvQty).setVisibility(View.GONE);
		}
	}

	@Override
	public void updateTotalSum(int sum, int weight, int count) {
		View v = findViewById(R.id.tvTotalSum);
		if( v != null )
			v.setVisibility(View.GONE);
	}
}
