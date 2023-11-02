package com.grsoft.napoleon;

import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DeliveryDetailEx extends DeliveryDetail {
	
	@Override protected DeliveryItemsAdapter createItemsAdapter() { return new DeliveryAdapterEx(); }
	
	protected String makePackQtyStr(long iqty, String packLabel) {
		Price p = priceImpl.getData();
		int inPack = p.qtyInPack;
		if( inPack == 0 )
			inPack = Consts.QTY_SCALE;
		int qty = (int)(iqty * Consts.QTY_SCALE / inPack);
		String qtyText = Util.IntToScaleStr(qty, Consts.QTY_SCALE) + " " + packLabel;
		return qtyText;
	}
	
	class DeliveryAdapterEx extends DeliveryItemsAdapter {
		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			View v = super.getView(arg0, arg1, arg2);

			String qtyText;
			DeliveryItem item = (DeliveryItem) getItem(arg0);
			if(((PriceEx)priceImpl.getData()).boxed == 0) {
				qtyText = makePackQtyStr(item.qty, getString(R.string.box_lbl));
			} else {
				qtyText = Util.IntToScaleStr(item.qty, Consts.QTY_SCALE) + " " + getString(R.string.qty_lbl);
			}
			TextView tvQty = (TextView)v.findViewById(R.id.tvQty);
			tvQty.setText(qtyText);
			tvQty.setGravity(Gravity.RIGHT);
			return v;
		}
	}
}
