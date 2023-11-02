package com.grsoft.napoleon;

import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.DeliveryItemEx;
import com.grsoft.dataobjects.OrderItem;

import android.view.View;
import android.widget.TextView;

public class OrderDeliveryDetailEx extends OrderDeliveryDetail {
	@Override
	protected int getItemLayoitId() {
		return R.layout.orderdeliverydetail_list_rowex;
	}
	
	@Override
	protected void drawItem(View view, DeliveryItem dlvItem, OrderItem ordItem, int color) {
		super.drawItem(view, dlvItem, ordItem, color);
		TextView tv = (TextView) view.findViewById(R.id.tvRemark);
		
		String remark = "";
		
		if (dlvItem != null) {
			remark = ((DeliveryItemEx)dlvItem).remark; 
			tv.setText(remark);
		}
		
		tv.setVisibility(remark.trim().length() > 0 ? View.VISIBLE : View.GONE);
	}
}
