package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class RemnantsDetailEx extends RemnantsDetail {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		btnSend.setVisibility(View.GONE);
	}
	
	@Override
	protected RemnantItemsAdapter createAdapter() {
		return new Adapter();
	}
	
	class Adapter extends RemnantItemsAdapter {
		@Override
		protected View setView(View view, PriceImpl priceImpl, int qty, Object tag) {
			if (view == null)
				view = View.inflate(RemnantsDetailEx.this, R.layout.remnantsdetail_list_row, null);
			
			TextView tvName = (TextView)view.findViewById(R.id.tvName);
			linesController.prepareTextView(tvName);
			tvName.setText(priceImpl.getData().name);
						
			int inPack = priceImpl.getData().qtyInPack;
			if( inPack == 0 )
				inPack = Consts.QTY_SCALE;
			qty = (int)((long)qty * Consts.QTY_SCALE / inPack);
			String qtyText = Util.IntToScaleStr(qty, Consts.QTY_SCALE) + " ó.";
			
			TextView tvQty = (TextView)view.findViewById(R.id.tvQty);
			tvQty.setText(qtyText);
			
			view.setTag(tag);
			return view;
		}
	}
}
