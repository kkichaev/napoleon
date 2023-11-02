package com.grsoft.napoleon;

import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;


public class PriceCountEx extends PriceCount{
	private TextView tvPlan;
	private TextView tvFact;
	private TextView tvProgres;
	
	long discount;
	
	@Override
	protected int getContentViewId() {	return R.layout.pricecountex; }
	
	@Override
	protected void postOnCreate() {
		super.postOnCreate();
		
		tvPlan = (TextView) findViewById(R.id.tvPlan);
		tvFact = (TextView) findViewById(R.id.tvFact);
		tvProgres = (TextView) findViewById(R.id.tvProgres);
		
		if(WarehouseEx.planCash == null)
			WarehouseEx.initPlan(document);
		
		WarehouseEx.updateSelCash(document);
	}
	
	@Override protected long getSumValue() { return super.getSumValue() - discount;}
	
	@Override
	protected void refreshData() {
		if( document instanceof OrderImplEx ) {
			OrderItemEx oe = (OrderItemEx) ((OrderImplEx)document).findItem(price.getData().id);
			if(oe != null)
				discount = oe.discount;
			
			int vsbl = View.GONE;
			if(discount != 0) {
				vsbl = View.VISIBLE;
				TextView tv = (TextView)findViewById(R.id.tvDsc);
				tv.setText(Util.IntToScaleStr(discount, Consts.SUM_SCALE, Util.DEC_DELIM, false));
			}
			findViewById(R.id.trDiscount).setVisibility(vsbl);
		}
		
		super.refreshData();
		String id = price.getData().id;
		int plan = 0;
		int fact = 0;
		
		if(WarehouseEx.planCash.containsKey(id))
			plan = WarehouseEx.planCash.get(id);
		
		if(WarehouseEx.selCash.containsKey(id))
			fact = WarehouseEx.selCash.get(id);
		
		int pcn = 0;
		
		if(plan > 0)
			pcn = (int)((double)fact / plan * 100);
		
		tvPlan.setText(Util.IntToScaleStr(plan, Consts.QTY_SCALE));
		tvFact.setText(Util.IntToScaleStr(fact, Consts.QTY_SCALE));
		tvProgres.setText(String.format("%d %%", pcn));
	}
}
