package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.VisitEx;


public class VisitImplEx extends VisitImpl {
	public void postInit() {
		super.postInit();
		
		StockOrgImpl stock = new StockOrgImpl();
		((VisitEx)data).stock = stock.read("id", data.id) ? 1 : 0;
	};
}
