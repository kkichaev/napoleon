package com.grsoft.database;

public class PriceHitchingEx extends PriceHitching {
	@Override
	public void prepareReading() {
		super.prepareReading();
		final String sql = "update price set whQty = null";
		try {
			DataBaseManager.getDataBase().execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
