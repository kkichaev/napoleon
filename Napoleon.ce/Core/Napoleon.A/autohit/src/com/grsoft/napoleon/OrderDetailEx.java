package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.UnitItem;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import android.widget.TextView;

public class OrderDetailEx extends OrderDetail {
	
	PriceImpl price = new PriceImpl();
	
	@Override
	protected void drawItemQty(int color, OrderItem item, TextView tvQty) {
		PriceEx p = (PriceEx)price.getData();
		p.id = item.id;
		if( price.read() ) {
			String unitName = "", qtyText;
			
			int inPack = 0;
			for(UnitItem unitItem : p.units)
				if (unitItem.id.equals(((OrderItemEx)item).unit)) {
					unitName = unitItem.name;
					inPack = unitItem.inpack;
				}
			if( inPack == 0 )
				inPack = Consts.QTY_SCALE;
			if( inPack != Consts.QTY_SCALE) {
				int qty = (int)((long)item.qty * Consts.QTY_SCALE / inPack);
				qtyText = Util.IntToScaleStr(qty, Consts.QTY_SCALE) + " " + unitName;
			} else
				qtyText = Util.IntToScaleStr(item.qty, Consts.QTY_SCALE) + " " + unitName;
			
			tvQty.setText(qtyText);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		price.close();
	}
}
