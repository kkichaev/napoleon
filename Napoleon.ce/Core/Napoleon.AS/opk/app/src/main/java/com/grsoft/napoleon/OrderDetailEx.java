package com.grsoft.napoleon;

import android.view.Gravity;
import android.widget.TextView;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class OrderDetailEx extends OrderDetail {

	@Override
	protected void drawItemQty(int color, OrderItem item, TextView tvQty) {
		String qtyText;
		PriceEx p = (PriceEx) price.getData();
		qtyText = Util.IntToScaleStr(item.qty, Consts.QTY_SCALE);
		if( p.isWeight != 0 )
			qtyText += " êã";
		else
			qtyText += " øò";
		
		tvQty.setText(qtyText);
		tvQty.setGravity(Gravity.RIGHT);
		tvQty.setTextColor(color);
	}
}
