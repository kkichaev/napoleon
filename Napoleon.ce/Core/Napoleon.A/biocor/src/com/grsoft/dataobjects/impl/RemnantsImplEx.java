package com.grsoft.dataobjects.impl;

import android.content.Context;
import android.graphics.Color;

public class RemnantsImplEx extends RemnantsImpl {
	@Override
	public void editItem(long itemRowid, Context context) {
//		PriceImpl pi = new PriceImpl();
//		pi.read(itemRowid);
//		
//		int qty = (findItem(pi.getData().id) != null) ? 0 : 1 * Consts.QTY_SCALE;
//		if( updateQty(pi, qty, 0, false) && context instanceof DataSetNotify )
//			((DataSetNotify)context).notifyDataSetChanged();
//		
//		RemnantsDoc.instance().refreshDocSum(data.id);
//		pi.close();
	}
	
	@Override
	public int getItemColor() {
		return Color.GREEN;
	}
}
