package com.grsoft.napoleon;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Payment;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.Util;

public class MainEx extends Main {
	boolean loadedDebs = false;
	private HashMap<String, Boolean> outOrgs = new HashMap<String, Boolean>();
	private HashMap<String, Boolean> outPays = new HashMap<String, Boolean>();

	@Override
	protected void onResume() {
		loadedDebs = false;
		super.onResume();
	}
	
	
	private int getOrgColor(Org o) {
		if( !loadedDebs ) {
			loadedDebs = true;
			
			outOrgs.clear();
			outPays.clear();
			
			Calendar cal = Calendar.getInstance();
			Date blueDate = Util.getDate();
			cal.setTime(blueDate);
			cal.add(Calendar.DATE, -5);
			Date redDate = cal.getTime();
			
			putDelivery(redDate, true);
			putDelivery(blueDate, false);

			putPayments(redDate, true);
			putPayments(blueDate, false);
		}
		
		int color = Color.BLACK;
		
		OrgEx oe = (OrgEx) o;
		Boolean v1 = outOrgs.get(oe.id);
		Boolean v2 = outPays.get(oe.id);
		if( v1 != null || v2 != null )
			color = ((v1 != null && v1) || (v2 != null && v2)) ? Color.RED : Color.BLUE;
		
		return color;
	}

	private void putPayments(Date date, boolean isOlder) {
		SQLiteDatabase db = DataBaseManager.getDataBase();
		String table = DataObjectInfo.getInstance().getTableName(Payment.class);
		String sql = "SELECT ido, id FROM " + table + " WHERE sum > 0 and payDate <= ? GROUP BY ido, id";
		try {
			String[] args = { Long.toString(date.getTime()) };
			Cursor c = db.rawQuery(sql, args);
			while( c.moveToNext() ) {
				String key = c.getString(0);
				if( isOlder || !outPays.containsKey(key) )
					outPays.put(key, isOlder);
				
				key = c.getString(1);
				if( isOlder || !outOrgs.containsKey(key))
					outOrgs.put(key, isOlder);
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void putDelivery( Date date, boolean isOldest) {
		String table = DataObjectInfo.getInstance().getTableName(Delivery.class);			
		String sql = "SELECT id FROM " + table + " WHERE paydate <= ? and sumD > 0 GROUP BY id";
		
		String[] args = { Long.toString(date.getTime()) };

		try {
			SQLiteDatabase db = DataBaseManager.getDataBase();
			Cursor c = db.rawQuery(sql, args);
			while( c.moveToNext() ) {
				String key = c.getString(0);
				if( !outOrgs.containsKey(key) )
					outOrgs.put(key, (Boolean)isOldest);
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void drawOrg(Org oi, View view) {
		super.drawOrg(oi, view);

		if( orgSum.getData().sum < 0 || DocType.getCurDoc().isHasCreatedToday(oi.id) )
			return;
		
		TextView tv = (TextView)view.findViewById(R.id.tvOrgName);
		tv.setTextColor(getOrgColor(oi));
	}
}
