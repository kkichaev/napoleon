package com.grsoft.database;
import com.grsoft.aceteam.R;

import android.database.sqlite.SQLiteStatement;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.PODel;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class PODelHitching extends Hitching {

	SQLiteStatement deleteStm;
	public PODelHitching() {
		super(PODel.class, "PODel");
		
		try{
			String table = DataObjectInfo.getInstance().getTableName(Org.class);
			DbWriter.checkDBTable(Org.class);
			deleteStm = DataBaseManager.getDataBase().compileStatement("DELETE FROM [" + table + "] WHERE id=?");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		if (deleteStm != null){
			PODel poDel = (PODel) rawObject.createDataObject(PODel.class);
			deleteStm.bindString(1, poDel.id);
			deleteStm.execute();
		}
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
		if( deleteStm != null)
			deleteStm.close();
	}
}
