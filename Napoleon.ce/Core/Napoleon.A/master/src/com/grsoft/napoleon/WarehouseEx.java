package com.grsoft.napoleon;

import java.util.HashMap;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImpl;

import android.view.View;
import android.widget.ImageView;

public class WarehouseEx extends WarehouseNew {
	@Override protected int getItemLayoutId() { return R.layout.priceitemrowex; }

	@Override
	protected void setName(View view, Price p, int linesCount, PriceTreeNode node) {
		super.setName(view, p, linesCount, node);
		
		if(document instanceof OrderImpl && document.getId().length() > 0) {
			HashMap<String, Integer> actionItems = ((CostStrategyEx)CostStrategy.defaultInstance).getActionItems(document.getId(), document.getDate());
			
			ImageView iv = (ImageView)view.findViewById(R.id.iAction);
			if( iv != null ) {
				iv.setImageResource( (actionItems != null && actionItems.containsKey(p.id)) ? R.drawable.action : R.drawable.empty );
			}
		}
	}
}
