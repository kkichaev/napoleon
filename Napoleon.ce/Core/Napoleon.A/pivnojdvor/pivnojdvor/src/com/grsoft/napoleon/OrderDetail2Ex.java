package com.grsoft.napoleon;

import com.grsoft.dataobject.OrderItemEx;

import android.graphics.Color;

public class OrderDetail2Ex extends OrderDetailEx {
	protected void setAdapter(){
		lvItems.setAdapter(new OrderItemsAdapter() {
			protected int getItemColor(int pos){
				OrderItemEx item = (OrderItemEx) getItem(pos);
				
				return item.tag == 0 ? Color.BLACK : Color.RED;
			}
		});
	}
}
