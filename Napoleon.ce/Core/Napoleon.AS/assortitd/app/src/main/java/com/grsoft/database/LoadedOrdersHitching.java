package com.grsoft.database;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.Util;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteStatement;

public class LoadedOrdersHitching extends HitchOnSelect {
	int daysBefore;
	
	Map<Date, Date> lastDocs = new HashMap<Date, Date>();
	SQLiteStatement stmt;
	
	boolean starting = false;
	long startDate = 0;
	
	public LoadedOrdersHitching() {
		super(OrderEx.class, "LoadedOrders");
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		DataObject dobj = rawObject.createDataObject(dataObject);
		if(!starting) {
			String sql = "delete from [" +  dobj.getTableName() + "] where fromKIS <> 0 and created >" + Long.toString(startDate);
			try {
				DataBaseManager.getDataBase().execSQL(sql);
			} catch (Exception e) {
				e.printStackTrace();
			}
			starting = true;
		}
		
		OrderEx oe = (OrderEx) dobj;
		oe.fromKIS = 1;
		oe.created = getCreated(Util.getDayStart(oe.date));
		oe.params = (ParamState.ofExported | ParamState.ofProceeded);
		dbProxy.insertRecord(dobj);
	}
	
	private Date getCreated(Date date) {
		Date ld = lastDocs.get(date);
		if(ld == null) 
			ld = date;
		
		Date ret = new Date(ld.getTime() + 1000);
		if(stmt != null) {
			do {
				stmt.clearBindings();
				stmt.bindLong(1, ret.getTime());
				
				try {
					stmt.simpleQueryForString();
				} catch (Exception e) {
					break;
				}
				
				ret = new Date(ret.getTime() + 1000);
			} while(true);			
		}
		lastDocs.put(date, ret);
		return ret;
	}

	@Override
	public void onEnd() {
		super.onEnd();
		
		if(stmt != null) {
			stmt.close();
		}
		try {
			OrderDoc.instance().refreshDocSum();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}
}
