package com.grsoft.database;

import java.util.Date;
import java.util.HashSet;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class LoadOrdersHitching extends Hitching {
	HashSet<Long> usedCreated = new HashSet<Long>();
	
	public LoadOrdersHitching() {
		super(OrderEx.class, "LoadedOrders");
	}
	
	@Override
	public void prepareReading() {
		try {
			SQLiteDatabase db = DataBaseManager.getDataBase();
			String tableName = new OrderEx().getTableName();
			String sql = "delete from [" + tableName + "] where loadedFromKIS <> 0";
			db.execSQL(sql);
			
			Cursor c = db.rawQuery("select created from [" + tableName + "]", null);
			while(c.moveToNext())
				usedCreated.add(c.getLong(0));
			c.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		OrderEx dobj = (OrderEx) rawObject.createDataObject(dataObject);
		
		while(usedCreated.contains(dobj.created.getTime())) {
			Date newCr = new Date(dobj.created.getTime() + 1);
			dobj.created = newCr;
		}
		usedCreated.add(dobj.created.getTime());
		
				
		dobj.loadedFromKIS = 1;
		dobj.params |= (ParamState.ofExported | ParamState.ofProceeded);
		dbProxy.insertRecord(dobj);
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
		try{ OrderDoc.instance().refreshDocSum(); }catch(Exception e){ e.printStackTrace(); }
	}
}
