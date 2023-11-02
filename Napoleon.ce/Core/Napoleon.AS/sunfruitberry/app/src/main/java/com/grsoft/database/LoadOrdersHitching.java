package com.grsoft.database;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.Util;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class LoadOrdersHitching extends Hitching {
//	HashSet<Long> usedCreated = new HashSet<Long>();
	
	SQLiteStatement stmt;
	Map<Date, Date> lastDocs = new HashMap<Date, Date>();

	boolean deleted = false;
	public LoadOrdersHitching() {
		super(OrderEx.class, "LoadedOrders");
	
		DbWriter.checkDBTable(OrderEx.class);
		String sql = "select id from [" + (new OrderEx()).getTableName() + "] where created = ?";
		try {
			stmt = DataBaseManager.getDataBase().compileStatement(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		if(!deleted) {
			deleted = true;
			try {
				SQLiteDatabase db = DataBaseManager.getDataBase();
				String tableName = new OrderEx().getTableName();
				String sql = "delete from [" + tableName + "] where loadedFromKIS <> 0";
				db.execSQL(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		OrderEx dobj = (OrderEx) rawObject.createDataObject(dataObject);
						
		dobj.loadedFromKIS = 1;
		dobj.params |= (ParamState.ofExported | ParamState.ofProceeded);
		dobj.created = getCreated(Util.getDayStart(dobj.date));
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
		try{ OrderDoc.instance().refreshDocSum(); }catch(Exception e){ e.printStackTrace(); }
	}
}