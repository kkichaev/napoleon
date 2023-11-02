package com.grsoft.database;

import android.annotation.SuppressLint;
import com.grsoft.dataobjects.Price;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;


@SuppressLint("DefaultLocale")
public class PriceHitchingEx extends PriceHitching{

	@Override
	public void prepareReading() {
		super.prepareReading();
		final String sql = "update price set hidden = 1";
		DataBaseManager.getDataBase().execSQL(sql);
	}
		
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		Price dobj = (Price)rawObject.createDataObject(dataObject);
		dobj.srchName = dobj.name.toUpperCase();
		dobj.hidden = 0;
		dbProxy.insertRecord(dobj);
	}
}
