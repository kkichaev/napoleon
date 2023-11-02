package com.grsoft.napoleon;

import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Filter;

public class WarehouseNew extends WarehouseNewW {
	
	@Override
	protected void adapterInit() {
		adapter.putFilter(new LivePriceFilter());
		super.adapterInit();
	}
	
	@Override
	protected boolean isShowDailySales() {
		CfgNpl cfg =  (CfgNpl) ConfigManager.getConfig();
		return cfg.showDailySales;
	}
}

class LivePriceFilter extends Filter{
	private static final String NAME = "LivePriceFilter";
	public LivePriceFilter() {
		super(NAME);
		DbWriter.checkDBTable(DbObject.getDataType(Price.class));
	}
	
	@Override
	public String getWhereStr() {
		CfgNpl cfg = (CfgNpl) ConfigManager.getConfig();
		return cfg.onlyNewstItems == 1 ? "hidden = 0 or hidden is null" : "";
	}
	
}
