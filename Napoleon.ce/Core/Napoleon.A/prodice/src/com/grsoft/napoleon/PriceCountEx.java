package com.grsoft.napoleon;

import com.grsoft.dataobjects.PriceEx;
import com.grsoft.util.InputNumber;

public class PriceCountEx extends PriceCount {
	@Override
	protected void doCostChange() {
		CostInputDlg.open(this, new InputNumber() {
			@Override public void applayInput(int value, Object... params) { onChangeCost(value); }
			@Override public int getValue() { return priceVal; }		
		}, ((PriceEx)price.getData()).minCost); 
	}
}
