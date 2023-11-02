package com.grsoft.napoleon;

import android.view.View;
import android.widget.TextView;
import com.grsoft.dataobjects.RemnantItemEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;


public class RemnantsDetailEx extends RemnantsDetail {
	@Override
	protected int getLayoutId() { return R.layout.remnantsdetailex; }
	
	protected RemnantItemsAdapter createAdapter() {
		return new RemnantItemsAdapter(){
			@Override
			protected View setView(View view, PriceImpl priceImpl, int qty, Object tag) {
				view = super.setView(view, priceImpl, qty, tag);
				
				if (view != null){
					RemnantItemEx i = (RemnantItemEx) remnantsImpl.findItem(priceImpl.getData().id);
					
					if(i != null){
						TextView tv = (TextView) view.findViewById(R.id.tvQtySh);
						
						if(tv != null)
							tv.setText(Util.IntToScaleStr(i.qtySh, Consts.QTY_SCALE));
						
						tv = (TextView) view.findViewById(R.id.tvQtyWh);
						
						if(tv != null)
							tv.setText(Util.IntToScaleStr(i.qtyWh, Consts.QTY_SCALE));
					}
				}
				
				return view;
			}
			
			@Override
			protected int getViewId() { return R.layout.remnantsdetail_list_rowex; }
		};
	}
}
