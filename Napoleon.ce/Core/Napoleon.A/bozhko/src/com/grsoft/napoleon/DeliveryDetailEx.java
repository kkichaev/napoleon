package com.grsoft.napoleon;

import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DeliveryDetailEx extends DeliveryDetail {
	
	@Override
	protected DeliveryItemsAdapter createItemsAdapter() {
		return new Adapter();
	}
	
	class Adapter extends DeliveryItemsAdapter {
		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			View v = super.getView(arg0, arg1, arg2);
			DeliveryItem item = (DeliveryItem) getItem(arg0);
			int qip = priceImpl.getData().qtyInPack;
			if( qip == 0 )
				qip = 1;
			int qty = item.qty * Consts.QTY_SCALE / qip;
			
			TextView tvQty = (TextView)v.findViewById(R.id.tvQty);
			tvQty.setText(Util.IntToScaleStr(qty, Consts.QTY_SCALE));
			return v;
		}
	}
}
