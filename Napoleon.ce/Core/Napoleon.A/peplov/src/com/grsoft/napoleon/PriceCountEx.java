package com.grsoft.napoleon;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount {
	int dsc;
	int minCost;
	int priceCost;
	
	Boolean canChangeCost = null;
	
	@Override protected int getContentViewId() { return R.layout.pricecountex; }
	
	@Override
	protected boolean canChangeCost() {
		if(!(document != null && document instanceof OrderImplBase<?>) )
			return false;
		
		if(canChangeCost == null) {
			canChangeCost = false;
	        ConfigImpl config = new ConfigImpl();
			config.getData().key = "МожноИзменятьЦену";
			try {
				if (config.read() && Integer.parseInt(config.getData().value) == 1)
					canChangeCost = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			config.close();
		}
		return canChangeCost;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		if( canChangeCost() ) {
			findViewById(R.id.trDiscount).setVisibility(View.VISIBLE);
			TextView tv = (TextView)findViewById(R.id.tvPrice);
			updateCost();
	
			PriceEx p = (PriceEx) price.getData();
			minCost = p.minCost;
	
			int sumType = document != null ? document.getSumType() : 0;
			priceCost = (p.cost.size() > sumType && sumType >= 0) ? 
					p.cost.get(sumType).cost : 0;			
	
			if(priceCost != 0)
				dsc = (int)(1000 - (long)priceVal * 1000 / priceCost);
			else
				dsc = 0;
			
			tv.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) { 
					CostInputDlg.open(PriceCountEx.this, new InputNumber() {
						@Override public void applayInput(int value, Object... params) { onChangeCost(value); }
						@Override public int getValue() { return priceVal; }		
					}, minCost); 
				}
			});
			
			
			tv = (TextView)findViewById(R.id.tvDiscount);
			updateNac();
			
			tv.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) { 
					InputNumberDlg.open(PriceCountEx.this, new InputNumber() {
						@Override public void applayInput(int value, Object... params) { onNacChange(value); }
						@Override public int getValue() { return dsc; }		
					}, 10, false, "Наценка"); 
				}
			});
		}
	}

	void updateNac() {
		TextView tv = (TextView)findViewById(R.id.tvDiscount);
		SpannableString ss = new SpannableString(Util.IntToScaleStr(dsc, 10, Util.DEC_DELIM, false));
		ss.setSpan(new UnderlineSpan(), 0, ss.length(), 0);
		tv.setText(ss);
	}
	
	@Override
	protected void onChangeCost( int newCost ) {
		priceVal = newCost;
		Price p = price.getData();

		if( p.cost.get(0).cost != 0 )
			dsc = (int)(1000 - (long)priceVal * 1000 / priceCost);

		updateCost();
		updateSumTextView();
		updateNac();
	}
	
	void onNacChange( int newNac ) {
		dsc = newNac;
		priceVal = (int)(((long)priceCost * (1000 - dsc)) / 1000);
		
		updateCost();
		updateSumTextView();
		updateNac();
	}
}
