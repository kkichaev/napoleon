package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.provider.SyncStateContract.Constants;
import android.widget.TextView;
import android.widget.Toast;

public class PriceCountEx extends PriceCount {
	int minCost, maxCost;
	private static final int DEFAULT_DISC_RANGE = 10 * Consts.SUM_SCALE;
	private static final String DISC_RANGE_KEY = "ƒиапазон÷ены";
	
	
	@Override protected int getContentViewId() { return R.layout.pricecountex; }

	@Override
	protected void refreshData() {
		super.refreshData();
		
		boolean sv = Features.CAN_CHANGE_COST;
		Features.CAN_CHANGE_COST = false;
		int cost = CostStrategy.defaultInstance.getItemCost(price.getData(), document);
		Features.CAN_CHANGE_COST = sv;
		
		int discRange = DEFAULT_DISC_RANGE;
		
		ConfigImpl cfg = new ConfigImpl();
		StringBuilder sb = new StringBuilder();
		if (cfg.getValue(sb, DISC_RANGE_KEY)) {
			try {
				discRange = Integer.parseInt(sb.toString().trim()) * Consts.SUM_SCALE;
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		minCost = CostStrategy.costWithDiscount(cost, discRange, Consts.SUM_SCALE);
		maxCost = CostStrategy.costWithDiscount(cost, -discRange, Consts.SUM_SCALE);
		TextView tv;
		
		tv = (TextView)findViewById(R.id.tvMinPrice);
		tv.setText(Util.IntToScaleStr(minCost, Consts.SUM_SCALE, Util.DEC_DELIM, false));

		tv = (TextView)findViewById(R.id.tvMaxPrice);
		tv.setText(Util.IntToScaleStr(maxCost, Consts.SUM_SCALE, Util.DEC_DELIM, false));
	}
	
	@Override
	protected void onChangeCost(int newCost) {
		if(newCost < minCost || newCost > maxCost) {
			Toast.makeText(this, "»зменение цены больше допустимого", Toast.LENGTH_SHORT).show();
			return;
		}
		super.onChangeCost(newCost);
	}
}
