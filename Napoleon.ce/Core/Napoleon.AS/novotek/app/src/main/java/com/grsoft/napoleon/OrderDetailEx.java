package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.Consts;

import android.view.View;
import android.widget.TextView;

public class OrderDetailEx extends OrderDetail {
	@Override
	protected void setContentView(){
		setContentView(R.layout.orderdetailex);
	}
	
	@Override
	protected void setAdapter(){
		lvItems.setAdapter(new OrderItemsAdapter(){
			protected void drawInternal(View view, String name, int color, OrderItem item, int pos) {
				super.drawInternal(view, name, color, item, pos);
				boolean restLessZero = ((OrderItemEx)item).outQty > 0;
				
//				if(price.read("id", item.id)) {
//					restLessZero = (doc.getItemValue(price.getData()) - item.getQty()) <= 0;
//				}
				
				view.setBackgroundResource(restLessZero ? R.drawable.list_grey_selector : R.drawable.list_selector);	
			}
		});
	}
	
	@Override
	public void updateTotalSum(long sum, int weight, int count) {
		TextView tvSumRest = (TextView) findViewById(R.id.tvSumRest);
		int s = 0;
		
		for (OrderItem oi: doc.getData().items) {
			int ios = ((OrderItemEx)oi).outQty; 
			if(ios <= 0)
				s += (long)oi.cost * oi.qty / Consts.QTY_SCALE;
			else if(oi.qty > ios)
				s += (long)oi.cost * (oi.qty - ios) / Consts.QTY_SCALE;
		}
		
		String sumStr = DocType.SumConverter.toString(s);
		tvSumRest.setText(sumStr);
		
		super.updateTotalSum(sum, weight, count);
	}
}
