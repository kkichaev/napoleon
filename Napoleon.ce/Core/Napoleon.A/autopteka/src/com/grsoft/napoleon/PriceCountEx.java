package com.grsoft.napoleon;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.Price;
import com.grsoft.util.Consts;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount {
	
	int nac;
	int minCost;
	int stopCost;

	@Override protected int getContentViewId() { return R.layout.pricecountex; }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		TextView tv = (TextView)findViewById(R.id.tvPrice);
		updateCost();

		Price p = price.getData();
		minCost = p.cost.get(1).cost;
		stopCost = (p.cost.size()>2) ? p.cost.get(2).cost : p.cost.get(0).cost;

		tv.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { 
				CostInputDlg.open(PriceCountEx.this, new InputNumber() {
					@Override public void applayInput(int value, Object... params) { onChangeCost(value); }
					@Override public int getValue() { return priceVal; }		
				}, minCost, stopCost); 
			}
		});
		
		nac = 0;
		if( p.cost.get(0).cost != 0 )
			nac = (int)((long)priceVal * 1000 / p.cost.get(0).cost - 1000);
		
		tv = (TextView)findViewById(R.id.tvDiscount);
		updateNac();
		
		tv.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { 
				InputNumberDlg.open(PriceCountEx.this, new InputNumber() {
					@Override public void applayInput(int value, Object... params) { onNacChange(value); }
					@Override public int getValue() { return nac; }		
				}, 10, false, "Наценка"); 
			}
		});
	}
	
	@Override
	protected void updateCost() {
		TextView tv = (TextView)findViewById(R.id.tvPrice);
		SpannableString ss = new SpannableString(Util.IntToScaleStr(priceVal, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		ss.setSpan(new UnderlineSpan(), 0, ss.length(), 0);
		tv.setText(ss);
	}
	
	void updateNac() {
		TextView tv = (TextView)findViewById(R.id.tvDiscount);
		SpannableString ss = new SpannableString(Util.IntToScaleStr(nac, 10, Util.DEC_DELIM, false));
		ss.setSpan(new UnderlineSpan(), 0, ss.length(), 0);
		tv.setText(ss);
	}
	
	@Override
	protected void onChangeCost( int newCost ) {
		priceVal = newCost;
		Price p = price.getData();

		if( p.cost.get(0).cost != 0 )
			nac = (int)((long)priceVal * 1000 / p.cost.get(0).cost - 1000);

		updateCost();
		updateSumTextView();
		updateNac();
	}
	
	void onNacChange( int newNac ) {
		nac = newNac;
		Price p = price.getData();
		priceVal = (int)(((long)p.cost.get(0).cost * (1000 + nac)) / 1000);
		
		updateCost();
		updateSumTextView();
		updateNac();
	}
}
