package com.grsoft.napoleon;

import android.view.View;
import android.widget.ImageView;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.PriceImpl;

import java.util.HashSet;
import java.util.Set;

public class WarehouseEx extends Warehouse {

	Set<String> actions = new HashSet<>();
	PriceImpl priceCache = new PriceImpl();

	@Override protected int getItemLayoutId() { return R.layout.priceitemrowex; }

	@Override
	protected void setName(View view, Price p, int linesCount, PriceTreeNode node) {
		super.setName(view, p, linesCount, node);
		ImageView iv = (ImageView)view.findViewById(R.id.iAction);
		if( iv != null ) {
			iv.setImageResource( actions.contains(p.id) ? R.drawable.action : R.drawable.empty );
		}
	}

	@Override
	protected void readDocument() {
		super.readDocument();
		actions.clear();
		if(document instanceof OrderImpl) {
			actions.addAll(CostStrategyEx.getActionItems((Order) document.getData()));
		}
	}
}
