package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.grsoft.dataobjects.AgentProcent;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		CheckBox cb = (CheckBox)findViewById(R.id.cbKG);
		cb.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean checked) {
				updateCost();
				updateSumTextView();
			}
		});
		
		if( document instanceof OrderImplBase<?> )
			((OrderImplBase<?>)document).setUpdateQtyHandler(new OrderImplBase.UpdateQtyHandler() {

				@Override
				public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
					OrderItemEx oe = (OrderItemEx)item;
					EditText ed = (EditText)findViewById(R.id.edRemark);
					oe.remark = ed.getText().toString();
					oe.inKG = ((CheckBox)findViewById(R.id.cbKG)).isChecked() ? 1 : 0;
				}
			});
	}
	
	@Override
	protected int getInputCost(Price p) {
		int cost = super.getInputCost(p); 
		CheckBox cb = (CheckBox)findViewById(R.id.cbKG);
		if(cb.isChecked()) {
			double val = cost / ((double)(price.getData().weight) / Consts.WEIGHT_SCALE) + 0.5;
			cost = (int)val;
		}			
		return cost;
	}
	
	@Override
	protected int getContentViewId() {
		return R.layout.pricecountex;
	}
	
	
	void updatePrcText(long ordsum) {
		double prc = AgentProcent.getProcent() / (100.0 * Consts.SUM_SCALE);
		double sum = ordsum * prc;
		
		TextView tv = (TextView) findViewById(R.id.tvPrcInfo);
		if( sum == 0 )
			tv.setVisibility(View.GONE);
		else {
			tv.setVisibility(View.VISIBLE);
			String earn = getString(R.string.your_earn);
			String text = String.format(earn, Util.IntToScaleStr((long)(sum * Consts.SUM_SCALE), Consts.SUM_SCALE, Util.DEC_DELIM, false ));
			if( tv != null) {
				tv.setText(text);
				findViewById(R.id.llPrcInfo).setVisibility(View.VISIBLE);
			}
		}
	}
	@Override
	protected long getSum(int count) {
		long sum = super.getSum(count); 
		updatePrcText(sum);
		return sum;
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();		

		if( document != null && document instanceof OrderImpl ) {
			OrderItemEx oe = (OrderItemEx)((OrderImpl)document).findItem(price.getData().id);
			if( oe != null ) {
				if ( oe.remark != null ) {
					EditText ed = (EditText)findViewById(R.id.edRemark);
					ed.setText(oe.remark);
				}
				CheckBox cb = (CheckBox)findViewById(R.id.cbKG);
				cb.setChecked( oe.inKG > 0 );
				updateCost();
				updateSumTextView();
			}
		}
		btnOK.setEnabled(priceVal > 0);
	}
}
