package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;

import com.grsoft.dataobjects.OrderItem;

public class OrderDetailEx extends OrderDetail {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		View v = findViewById(R.id.TextView03);
		
		if(v != null)
			v.setVisibility(View.GONE);
	}
	
	@Override
	protected void setAdapter() {
		lvItems.setAdapter(new OrderItemsAdapter(){
			@Override
			protected void drawInternal(View view, String name, int color,
					OrderItem item) {
				super.drawInternal(view, name, color, item);
				View tvSum = view.findViewById(R.id.tvSum);
				
				if(tvSum != null)
					tvSum.setVisibility(View.GONE);
			}
		});
	}
}
