package com.grsoft.napoleon;

import android.view.Gravity;
import android.widget.TextView;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceUnit;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class OrderDetailEx extends OrderDetail {
	@Override
	protected void drawItemQty(int color, OrderItem item, TextView tvQty) {
		int inPack = 0;
		String qtyText, packName = "";
		PriceEx p = (PriceEx) price.getData();
		for(PriceUnit ui : p.units) {
			if( ui.id.equals(((OrderItemEx)item).unitId) ) {
				inPack = ui.inPack;
				packName = ui.name;
				break;
			}				
		}
		if( inPack == 0 )
			inPack = Consts.QTY_SCALE;
		int qty = (int)((long)item.qty * Consts.QTY_SCALE / inPack);

		qtyText = Util.IntToScaleStr(qty, Consts.QTY_SCALE) + " " + packName;
		
		tvQty.setText(qtyText);
		tvQty.setGravity(Gravity.RIGHT);
		tvQty.setTextColor(color);
	}
}
