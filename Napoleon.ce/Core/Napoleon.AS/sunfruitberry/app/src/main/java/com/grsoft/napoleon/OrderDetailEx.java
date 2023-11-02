package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.view.View;
import android.widget.TextView;

public class OrderDetailEx extends OrderDetail {
	@Override
	protected void setContentView() {
		setContentView(R.layout.orderdetailex);
	}
	
	@Override
	protected void setAdapter() {
		lvItems.setAdapter(new Adapter());
	}
	
	class Adapter extends OrderItemsAdapter {
		@Override int getResourceID() { return R.layout.orderdetail_row_ex; }
		
		@Override
		protected void drawInternal(View view, String name, int color, OrderItem item, int pos) {
			super.drawInternal(view, name, color, item, pos);
			TextView tv = (TextView)view.findViewById(R.id.tvCost);
			tv.setText(Util.IntToScaleStr(item.cost, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		}
	}
}
