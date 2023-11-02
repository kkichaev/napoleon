package com.grsoft.database;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.RemovedReturn;
import com.grsoft.dataobjects.Return;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;
import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class RemovedReturnHitching extends Hitching {
	protected SQLiteStatement retStmt;
	SimpleDateFormat parser;

	@SuppressLint("SimpleDateFormat")
	public RemovedReturnHitching() {
		super(RemovedReturn.class, "RemovedReturns");
		parser = new SimpleDateFormat("yyyyMMddHHmmss");
	}
	
	@Override
	public void onStart() {
		SQLiteDatabase database = DataBaseManager.getDataBase();
		String table = DataObjectInfo.getInstance().getTableName(Return.class);
		retStmt = database.compileStatement("UPDATE " + table + " set retNumber='' where created=?");
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		RemovedReturn dobj = (RemovedReturn)rawObject.createDataObject(dataObject);
		
		try {
			if( dobj.created.length() == 0 || dobj.created.toUpperCase(Locale.getDefault()).equals("NONE"))
				return;

			Date created = parser.parse(dobj.created);
			retStmt.clearBindings();
			retStmt.bindLong(1, created.getTime());
			retStmt.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void onEnd() {
		if( retStmt != null )
			retStmt.close();
	}
}
