package com.grsoft.napoleon;

import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.RemnantItemEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class RemnantsDetailEx extends RemnantsDetail {
	@Override
	protected int getLayoutId() {
		return R.layout.remnantsdetailex;
	}
	
	protected RemnantItemsAdapter createAdapter() {
		return new RemnantItemsAdapter(){
			@Override
			protected View setView(View view, PriceImpl priceImpl, int qty,
					Object tag) {
				View result = super.setView(view, priceImpl, qty, tag);
				RemnantItemEx item = (RemnantItemEx)tag;
				TextView tvFace = (TextView) result.findViewById(R.id.tvFace);
				tvFace.setText(Util.IntToScaleStr(item.cost, Consts.SUM_SCALE));
				return result;
			}
			
			@Override
			protected int getViewId() {
				return R.layout.remnantsdetail_list_rowex;
			}
		};
		
		
	};
}
