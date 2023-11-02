package com.grsoft.database;

import com.grsoft.dataobjects.Price;


public class PriceHitching extends PriceHitchingW{

	@Override
	public void prepareReading() {
		super.prepareReading();
		DbWriter.checkDBTable(Price.class);
		final String sql = "update price set hidden = 1";
		DataBaseManager.getDataBase().execSQL(sql);
	}
		
	@Override
	protected void beforeInsert(Price dobj) {
		super.beforeInsert(dobj);
		dobj.hidden = 0;
	}
}
