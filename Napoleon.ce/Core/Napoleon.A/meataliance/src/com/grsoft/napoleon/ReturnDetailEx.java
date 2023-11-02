package com.grsoft.napoleon;



public class ReturnDetailEx extends ReturnDetail {
	protected void setAdapter(){
		lvItems.setAdapter(new OrderItemsAdapter(){
			@Override
			int getResourceID() { return R.layout.orderdetail_list_rowex; }
		});
	}
}
