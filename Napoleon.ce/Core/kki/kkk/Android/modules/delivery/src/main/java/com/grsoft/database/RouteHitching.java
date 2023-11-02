package com.grsoft.database;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Route;
import com.grsoft.dataobjects.RouteItem;
import com.grsoft.util.Util;

import android.database.SQLException;
import android.database.sqlite.SQLiteStatement;

public class RouteHitching extends Hitching {
	
	SQLiteStatement stmt;
	
	public RouteHitching() {
		super(Route.class, DataObjectInfo.getInstance().getSrvName(Route.class));
	}
	
	@Override
	public void onStart() {
		super.onStart();
		try {
			String sql = "DELETE FROM " + (new RouteItem()).getTableName() + " WHERE route=?";
			stmt = DataBaseManager.getDataBase().compileStatement(sql);
			
			long dt = Util.getDate().getTime() - 2 * 24 * 3600 * 1000; 
			sql = "update " + (new Route()).getTableName() + " set hidden = 1 where finish >= " + Long.toString(dt);
			DataBaseManager.getDataBase().execSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void postRead(DataObject dobj) {
		super.postRead(dobj);
		if(stmt != null) {
			try {
				stmt.clearBindings();
				stmt.bindString(1, ((Route)dobj).id);
				stmt.execute();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onEnd() {
		super.onEnd();
		if(stmt != null) {
			stmt.close();
		}
	}
}
