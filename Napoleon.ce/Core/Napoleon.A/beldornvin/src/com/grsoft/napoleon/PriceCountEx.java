package com.grsoft.napoleon;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class PriceCountEx extends PriceCount implements OrderImplBase.UpdateQtyHandler {
	
	int priceCost = 0;
	int promoCost = 0;
	
	static final int ACT_FLAG = 0x100;
	
	@Override protected int getContentViewId() { return R.layout.pricecountex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CheckBox cb = (CheckBox)findViewById(R.id.cbAct);
		cb.setOnCheckedChangeListener(new  CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if(promoCost != 0)
					onChangeCost(arg1 ? promoCost : priceCost);
			}
		});
		
		if(document instanceof OrderImpl)
			((OrderImpl)document).setUpdateQtyHandler(this);
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();
		
		if(document instanceof OrderImpl) {
			PriceEx pe = (PriceEx) price.getData();
			if(pe.promoCost != 0) {
				findViewById(R.id.trActCost).setVisibility(View.VISIBLE);
				findViewById(R.id.trCBAct).setVisibility(View.VISIBLE);
				
				promoCost = pe.promoCost;
				priceCost = priceVal;
				
				TextView tv = (TextView)findViewById(R.id.tvActPrice);
				tv.setText(Util.IntToScaleStr(promoCost, Consts.SUM_SCALE, Util.DEC_DELIM, false));
				
				OrderItem oi = (OrderItem) ((OrderImpl)document).findItem(pe.id);
				if(oi != null && (oi.flags & ACT_FLAG) == ACT_FLAG) {
					((CheckBox)findViewById(R.id.cbAct)).setChecked(true);
					onChangeCost(promoCost);
				}
			}
		}
	}

	@Override
	public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
		CheckBox cb = (CheckBox)findViewById(R.id.cbAct);
		if(cb.isChecked()) {
			item.flags |= ACT_FLAG;
		} else {
			item.flags &= (~(ACT_FLAG));
		}
		
	}
}
