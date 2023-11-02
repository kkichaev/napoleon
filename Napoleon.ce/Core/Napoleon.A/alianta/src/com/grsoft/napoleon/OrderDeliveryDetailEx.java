package com.grsoft.napoleon;

import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.napoleon.OrderDeliveryDetail.OrderDeliveryItemsAdapter;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.graphics.Color;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

public class OrderDeliveryDetailEx extends OrderDeliveryDetail {
	@Override
	protected void drawItem(View view, DeliveryItem dlvItem, OrderItem ordItem, int color) {
		if(dlvItem != null) {
			TextView tvSum = (TextView)view.findViewById(R.id.tvSum);						
			long sum = dlvItem.sum;
			long cost = dlvItem.qty > 0 ? sum * Consts.QTY_SCALE / dlvItem.qty : 0;
			String text = Util.IntToScaleWStr(sum, Consts.SUM_SCALE, Consts.PRICE_DEC_WIDTH, false);
			text += "<br/><i>" + Util.IntToScaleWStr(cost, Consts.SUM_SCALE, Consts.PRICE_DEC_WIDTH, false) + "</i>";
			tvSum.setText(Html.fromHtml(text));
			tvSum.setGravity(Gravity.RIGHT);
			tvSum.setTextColor(color);
		}
	}
	
	@Override
	protected void setAdapter() {
		lvItems.setAdapter(new Adapter());
	}
	
	class Adapter extends OrderDeliveryItemsAdapter {
		
		@Override
		protected int getItemColor(OrderItem item, int defaultColor) {
			int color = super.getItemColor(item, defaultColor);
			if(color == defaultColor && currentItem != null && currentItem.sum != (((long)item.cost * item.qty) / Consts.QTY_SCALE)) {
				color = Color.MAGENTA;
			}
			return color;
		}
	}
}
