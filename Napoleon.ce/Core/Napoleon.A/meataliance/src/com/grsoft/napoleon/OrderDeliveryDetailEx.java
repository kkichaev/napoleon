package com.grsoft.napoleon;

import android.view.View;
import com.grsoft.dataobjects.OrderItem;



public class OrderDeliveryDetailEx extends OrderDeliveryDetail {
	@Override
	protected void setAdapter() {
		lvItems.setAdapter(new OrderDeliveryItemsAdapter(){
			@Override
			int getResourceID() { return R.layout.orderdeliverydetail_list_rowex; }
			
			@Override
			protected void drawInternal(View view, String name, int color, OrderItem item) {
				super.drawInternal(view, name, color, item);
				
				view.findViewById(R.id.vDelimeter).setBackgroundColor(getResources().getColor(R.color.blue));
			}
		});
	}
}
