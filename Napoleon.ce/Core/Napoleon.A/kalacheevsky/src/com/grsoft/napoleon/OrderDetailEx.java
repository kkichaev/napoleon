package com.grsoft.napoleon;

import android.widget.TextView;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.UnitItem;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class OrderDetailEx extends OrderDetail {
	
	PriceImpl price = new PriceImpl();
	
	@Override
	protected void drawItemQty(int color, OrderItem item, TextView tvQty) {
		boolean showPack = (item.inPack() && ((CfgNpl)ConfigManager.getConfig()).isPackView);
		if( !showPack ) {
			super.drawItemQty(color, item, tvQty);
			return;
		}
		
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
	protected void updateTotalSum() {
		updateTotalSum(doc.sum(), doc.weight(), 0);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		price.close();
	}
}
