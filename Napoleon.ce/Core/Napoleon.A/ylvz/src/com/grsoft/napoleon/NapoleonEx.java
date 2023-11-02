package com.grsoft.napoleon;

import java.util.Date;
import java.util.HashSet;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.impl.OrgImpl;

public class NapoleonEx extends Napoleon {
	public static HashSet<String> debtOrgs = null;
	
	void loadSet(HashSet<String> set, Date checkDate) {
		set.clear();
		
		SQLiteDatabase db = DataBaseManager.getDataBase();
		DbWriter.checkDBTable(Delivery.class);
		String table = DataObjectInfo.getInstance().getTableName(Delivery.class);			
		String sql = "SELECT DISTINCT id FROM " + table + " WHERE paydate <= ? and sumD <> 0";		
		String[] args = { Long.toString(checkDate.getTime()) };
		try {
			Cursor c = db.rawQuery(sql, args);
			while( c.moveToNext() )
				set.add(c.getString(0));
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onResume() {
		Date d = new Date();
		if(debtOrgs == null){
			debtOrgs = new HashSet<String>();
			loadSet(debtOrgs, d);
		}
		
		super.onResume();
	}
	
	@Override
	protected void setOrgBackground(int pos, OrgImpl org, View v) {
		if( org == null ) {
			super.setOrgBackground(pos, org, v);
			return;
		}
		
		String id = org.getData().id;
		
		if(debtOrgs.contains(id)) 
			v.setBackgroundResource(R.drawable.red_row);
		else  
			super.setOrgBackground(pos, org, v);
	}
}
