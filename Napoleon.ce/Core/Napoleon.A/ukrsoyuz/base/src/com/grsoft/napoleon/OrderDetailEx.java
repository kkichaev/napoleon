package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.UnitItem;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class OrderDetailEx extends OrderDetail {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		btnSend.setVisibility(View.GONE);
	}

	@Override
	protected void setAdapter() {
		lvItems.setAdapter(new OrderItemsAdapterEx());
	}
	
	class OrderItemsAdapterEx extends OrderItemsAdapter{
		@Override
		protected void drawInternal(View view, String name, int color, OrderItem item) {
			super.drawInternal(view, name, color, item);
						
			PriceImpl priceImpl = new PriceImpl();
			priceImpl.getData().id = item.id;
			
			if(priceImpl.read()){
				String unitName = "";
				
				int inPack = 0;
				for(UnitItem unitItem : ((PriceEx)priceImpl.getData()).units)
					if (unitItem.id.equals(((OrderItemEx)item).unit)) {
						unitName = unitItem.name;
						inPack = unitItem.inpack;
					}
				
				TextView tvQty = (TextView)view.findViewById(R.id.tvQty);
				boolean showPack = ((CfgNpl)ConfigManager.getConfig()).isPackView;
				String qtyText;
				if( !showPack )
					qtyText = Util.IntToScaleStr(item.qty, Consts.QTY_SCALE);
				else {
					if( inPack == 0 )
						inPack = Consts.QTY_SCALE;
					if( inPack != Consts.QTY_SCALE) {
						int qty = (int)((long)item.qty * Consts.QTY_SCALE / inPack);
						qtyText = Util.IntToScaleStr(qty, Consts.QTY_SCALE) + " " + unitName;
					} else
						qtyText = Util.IntToScaleStr(item.qty, Consts.QTY_SCALE) + " " + unitName;
				}
				tvQty.setText(qtyText);
			}
			
			priceImpl.close();
		}
	}
	
}
