package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.impl.DistributorImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import android.os.Bundle;
import android.widget.Toast;

public class PriceCountEx extends PriceCount {
	private int minCost = 0; 
	private int disc = 0;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DistributorImpl distr = new DistributorImpl();
		
		if(document != null && DocType.getCurDoc() == OrderDoc.instance()){
			OrderEx oe = (OrderEx) document.getData();
			distr.read("id", oe.distr);
			disc = distr.getData().disc;
			
			if(disc != 0){
				CostStrategy c = CostStrategy.getInstance((Class<? extends Document<?>>) document.getClass());
				minCost = c.costWithDiscount(priceVal, distr.getData().disc, Consts.SUM_SCALE);
			}
		}
	}
	
	protected void doCostChange() {
		InputNumberDlg.open(this, new InputNumber() {
			@Override public void applayInput(int value, Object... params) { 
				if(disc == 0 || value >= minCost)
					onChangeCost(value);
				else
					showMinCostError();
			}
			
			@Override public int getValue() { return priceVal; }		
		}, Consts.SUM_SCALE, false, getString(R.string.cost)); 
	}

	protected void showMinCostError() {
		Toast.makeText(this, getString(R.string.disc_overhead,
				Util.IntToScaleStr(disc, Consts.SUM_SCALE), Util.IntToScaleStr(minCost, Consts.SUM_SCALE)), Toast.LENGTH_SHORT).show();
	}
}
