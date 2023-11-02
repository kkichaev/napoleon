package com.grsoft.napoleon;

import java.util.ArrayList;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.util.view.dialog_helper.DialogHelper;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class PriceCountEx extends PriceCount implements OrderImplBase.UpdateQtyHandler {
	
	int priceCost = 0;
	int actionCost = 0;
	String actionCostName = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(document instanceof OrderImpl)
			((OrderImpl)document).setUpdateQtyHandler(this);
	}
	
	@Override protected int getContentViewId() { return R.layout.pricecountex; }

	@Override
	protected void refreshData() {
		super.refreshData();
		
		priceCost = priceVal;
		actionCost = 0;
		
		Price p = price.getData();
		ConfigImpl ci = new ConfigImpl();
		StringBuilder sb = new StringBuilder(); 
		if(ci.getValue(sb, "јкционна€÷ена")) {
			int pos = Integer.parseInt(sb.toString());
			if(pos < p.cost.size()) {
				actionCost = p.cost.get(pos).cost;
				sb = new StringBuilder();
				ci.getValue(sb, "¬ид÷ены");
				ArrayList<CharSequence> values = new ArrayList<CharSequence>();
				DialogHelper.makeList(sb.toString(), values);
				actionCostName = values.get(pos).toString();
			}
		}
		CheckBox cb = (CheckBox)findViewById(R.id.cbAction);
		cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				onChangeCost(arg1 ? actionCost : priceCost);
			}
		});
		
		cb.setEnabled(actionCost > 0);
		
		OrderItemEx oie = (OrderItemEx) ((OrderImpl)document).findItem(p.id);
		if(oie != null) {
			cb.setChecked(oie.action > 0);
			onChangeCost(oie.cost);
		}
	}

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		OrderItemEx oie = (OrderItemEx)item;
		if(((CheckBox)findViewById(R.id.cbAction)).isChecked()) {
			oie.action = 1;
			oie.costName = actionCostName;
		} else {
			oie.action =  0;
			oie.costName = "";
		}
		
	}
}
