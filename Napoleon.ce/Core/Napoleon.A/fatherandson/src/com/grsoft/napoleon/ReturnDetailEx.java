package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceUnit;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.view.Gravity;
import android.widget.TextView;

public class ReturnDetailEx extends ReturnDetail {
	@Override
	protected void drawItemQty(int color, OrderItem item, TextView tvQty) {
		int inPack = 0;
		String qtyText, packName = "";
		PriceEx p = (PriceEx) price.getData();
		for(PriceUnit ui : p.units) {
			if( ui.id.equals(((ReturnItemEx)item).unitId) ) {
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
