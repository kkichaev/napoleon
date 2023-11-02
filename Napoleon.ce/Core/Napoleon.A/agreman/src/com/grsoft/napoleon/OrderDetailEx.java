package com.grsoft.napoleon;

import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.UnitItem;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class OrderDetailEx extends OrderDetail {
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.orderdetailex);
	}
	
	@Override
	protected void drawItemQty(int color, OrderItem item, TextView tvQty) {
		boolean showPack = (item.inPack() && ((CfgNpl)ConfigManager.getConfig()).isPackView);
		
		if( !showPack )
			super.drawItemQty(color, item, tvQty);
		else {
			int inPack = 0;
			String qtyText, packName = "";
			PriceEx p = (PriceEx) price.getData();
			for(UnitItem ui : p.units) {
				if( ui.id.equals(((OrderItemEx)item).unit) ) {
					inPack = ui.inpack;
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
	
	@Override
	protected void setAdapter() {
		lvItems.setAdapter(new ItemsAdpater());
	}
	
	class ItemsAdpater extends OrderItemsAdapter {
		
		@Override int getResourceID() { return R.layout.orderdetail_list_rowex; }
		
		@Override
		protected void drawInternal(View view, String name, int color, OrderItem item) {
			super.drawInternal(view, name, color, item);

			int dsc = ((OrderItemEx)item).discount;
			TextView tv;
			tv = (TextView)view.findViewById(R.id.tvDiscount);
			tv.setText(Util.IntToScaleStr(dsc, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		}
	}
}
