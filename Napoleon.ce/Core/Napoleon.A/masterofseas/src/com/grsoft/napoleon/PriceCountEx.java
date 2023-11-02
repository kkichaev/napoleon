package com.grsoft.napoleon;

import com.grsoft.dataobjects.PriceEx;

import android.widget.Toast;

public class PriceCountEx extends PriceCount {
	int minCost;
	
	@Override
	protected void refreshData() {
		super.refreshData();
		minCost = ((PriceEx)price.getData()).minCost;
	}
	
	@Override
	protected void onChangeCost(int newCost) {
		if(newCost < minCost) {
			Toast.makeText(this, "Цена ниже минимальной", Toast.LENGTH_SHORT).show();
			return;
		}
		super.onChangeCost(newCost);
	}
}
