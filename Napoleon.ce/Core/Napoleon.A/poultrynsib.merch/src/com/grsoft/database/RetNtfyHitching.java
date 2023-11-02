package com.grsoft.database;

import android.database.sqlite.SQLiteStatement;

import com.grsoft.dataobjects.RetNtfy;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class RetNtfyHitching extends Hitching {

	private SQLiteStatement stm;
	
	public RetNtfyHitching() {
		super(RetNtfy.class, "RetNtfy");
		DbWriter.checkDBTable(ReturnEx.class);
		stm = DataBaseManager.getDataBase().compileStatement("update return set forsake=0 where created=?");
	}

	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		RetNtfy data = (RetNtfy) rawObject.createDataObject(dataObject);
		stm.bindLong(1, data.created.getTime());
		stm.execute();
	}
}
