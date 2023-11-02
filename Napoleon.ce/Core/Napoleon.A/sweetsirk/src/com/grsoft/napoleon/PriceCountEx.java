package com.grsoft.napoleon;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase.UpdateQtyHandler;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class PriceCountEx extends PriceCount implements UpdateQtyHandler{
	TextView tvBaseCost;
	long basePrice = 0;
	View trCostWithDiscount;
	
	@Override protected int getContentViewId() { return R.layout.pricecount_newex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		trCostWithDiscount = findViewById(R.id.trCostWithDiscount);
		basePrice = ((CostStrategyEx)CostStrategy.defaultInstance).getBasePrice(price.getData(), document); 
		tvBaseCost = (TextView)findViewById(R.id.tvBaseCost);
		tvBaseCost.setText(Util.IntToScaleStr(basePrice, Consts.SUM_SCALE));
		
		
		if( document instanceof OrderImpl )
			((OrderImpl)document).setUpdateQtyHandler(this);
		
		int disc = 0;
		OrgImpl org = new OrgImpl();
		if(document != null && org.read("id", document.getId()))
			disc = ((OrgEx)org.getData()).disc;
		;
		
		trCostWithDiscount.setVisibility(((PriceEx)price.getData()).disc == 1 && disc > 0 ? 
				View.VISIBLE : View.GONE);
	}

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		OrderItemEx ie = (OrderItemEx)item;
		ie.disc = basePrice - priceVal;
	}
}
