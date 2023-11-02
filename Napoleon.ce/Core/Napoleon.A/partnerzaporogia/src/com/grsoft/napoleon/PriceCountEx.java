package com.grsoft.napoleon;

import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class PriceCountEx extends PriceCount {
	
	int addCost = 0;
	int minCost = 0;
	Boolean canChangeCost = null;
	
	@Override protected int getContentViewId() { return R.layout.pricecountex; }

	@Override
	protected boolean canChangeCost() {

		if(canChangeCost == null) {
			ConfigImpl config = new ConfigImpl();
			config.getData().key = "МожноИзменятьЦену";
			try {
				canChangeCost = (config.read() && Integer.parseInt(config.getData().value) != 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(canChangeCost) {
				StringBuilder sb = new StringBuilder();
				if(config.getValue(sb, "МинимальнаяНаценка")) {
					addCost = -Util.StrToScale(sb.toString(), Consts.SUM_SCALE);
				}
			}
			config.close();
		}
		return	(document instanceof OrderImpl)	&& canChangeCost;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(canChangeCost) {
			findViewById(R.id.trIncomeCost).setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	protected void onChangeCost(int newCost) {
		if(newCost < minCost) {
			Toast.makeText(this, "Цена меньше минимальной " + Util.IntToScaleStr(minCost, Consts.SUM_SCALE), Toast.LENGTH_SHORT).show();
			return;
		}
		super.onChangeCost(newCost);
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();
		
		if(canChangeCost) {
			PriceEx pe = (PriceEx)price.getData();
			TextView tv;
			tv = (TextView)findViewById(R.id.tvIncomePrice);
			tv.setText(Util.IntToScaleStr(pe.costpur, Consts.SUM_SCALE, Util.DEC_DELIM, false));
			minCost = CostStrategy.costWithDiscount(pe.costpur, addCost, Consts.SUM_SCALE);
		}
	}
}
