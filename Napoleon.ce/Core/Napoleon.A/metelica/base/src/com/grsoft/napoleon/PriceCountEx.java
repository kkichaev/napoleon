package com.grsoft.napoleon;

import com.grsoft.dataobjects.PriceEx;
import com.grsoft.util.Consts;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import com.grsoft.view.KeypadHelper;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class PriceCountEx extends PriceCount {

	
	@Override
	protected boolean canChangeCost() {
		return ((PriceEx)price.getData()).minCost > 0;
	}

	@Override
	protected void doCostChange() {
		final int minCost = ((PriceEx)price.getData()).minCost;
		InputNumberDlg.open(this, new InputNumber() {
			@Override public int getValue() { return priceVal; }		
			@Override public void applayInput(int value, Object... params) { 
				if(value < minCost) {
					Toast.makeText(PriceCountEx.this, "Цена меньше минимальной", Toast.LENGTH_LONG).show();
					return;
				}
				onChangeCost(value); 
			}
		}, Consts.SUM_SCALE, false, getString(R.string.cost), false, new InputNumberDlg.Decorator(){

			@Override public int getContentView() { return R.layout.inputnumberdlg; }

			@Override
			public void adjustView(AlertDialog dialog, View view, KeypadHelper nh) {
				TextView tv = (TextView) view.findViewById(R.id.tvDiscountInfo);
				String text = "Мин.цена: " + Util.IntToScaleStr(minCost, Consts.SUM_SCALE, Util.DEC_DELIM, false);
				tv.setText(text);
				tv.setVisibility(View.VISIBLE);
			}
		}); 
	}
}
