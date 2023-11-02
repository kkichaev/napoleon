package com.grsoft.napoleon;

import android.view.View;
import android.widget.Spinner;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.napoleon.modules.CostHelper;
import com.grsoft.napoleon.modules.CostManager.CostType;

public class CreateSalesEx extends CreateSales {
	@Override
	protected void init(Sales s, Org o) {
		OrgEx org = (OrgEx)o;
		((SalesEx)s).costCode = org.cost;
		s.sumType = Features.COST_MANAGER.getCostIndex(org.cost);
	}
	
	@Override
	protected void saveCost() {
	}
	
	@Override
	protected void loadCost() {
		View v = findViewById(R.id.trCost);
		if( v != null )
			v.setVisibility(View.VISIBLE);
		
		ConfigImpl config = new ConfigImpl();
		Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
		CostHelper.loadCostTypes(spPrices, ((SalesEx) salesImpl.getData()).costCode, new CostHelper.CostSelector() {
			
			@Override
			public void selectedCost(CostType costType, int index) {
				SalesEx oe = (SalesEx) salesImpl.getData();
				oe.costCode = costType.id;
				oe.sumType = index;
			}
		});

		config.getData().key = "ћожно»змен€ть÷ену";
		try {
			if (config.read() && Integer.parseInt(config.getData().value) == 0)
				spPrices.setEnabled(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		config.close();
	}
}
