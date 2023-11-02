package com.grsoft.napoleon;

import android.view.View;
import android.widget.TextView;
import com.grsoft.dataobjects.RemnantItemEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;


public class RemnantsDetailEx extends RemnantsDetail {
	@Override
	protected int getLayoutId() { return R.layout.remnantsdetailex;	}
	
	protected int getItemViewId(){	return R.layout.remnantsdetail_list_rowex; }
	
	protected RemnantItemsAdapter createAdapter() {
		return new RemnantItemsAdapter(){
			@Override
			protected View setView(View view, PriceImpl priceImpl, int qty, Object tag) {
				view =  super.setView(view, priceImpl, qty, tag);
				
				RemnantItemEx i = (RemnantItemEx) tag;
				
				TextView tv = (TextView) view.findViewById(R.id.tvTara);
				tv.setText(Util.IntToScaleStr(i.tara, Consts.QTY_SCALE));
				return view;
			}
		};
	}
}
