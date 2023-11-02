package com.grsoft.napoleon;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.IncassDebDistrEx;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.Visit;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

public class MainEx extends Main {
	public static HashSet<String> debtOrgs = null;

	Set<String> todayOrgs = new HashSet<String>();
	
	void loadSet(HashSet<String> set, Date checkDate) {
		set.clear();
		
		SQLiteDatabase db = DataBaseManager.getDataBase();
		DbWriter.checkDBTable(Delivery.class);
		String table = DataObjectInfo.getInstance().getTableName(Delivery.class);			
		String sql = "SELECT DISTINCT id FROM " + table + " WHERE paydate <= ? and sumD > 0";		
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
	protected void drawOrg(Org org, View view) {
		super.drawOrg(org, view);

		if(todayOrgs.contains(org.id)) {
			int color = getResources().getColor(R.color.item_highlight); 
			((TextView)view.findViewById(R.id.tvOrgName)).setTextColor(color);
		}
	}

	@Override
	protected void onResume() {
		Date d = new Date();
		debtOrgs = new HashSet<String>();
		loadSet(debtOrgs, d);
		
		todayOrgs.clear();
		
		Date start = Util.getDate();
		Date finish = Util.getDayEnd(start);
		
		String[] tables = new String[] {
				(new OrderEx()).getTableName(),
				(new IncassDebDistrEx()).getTableName(),
				(new Visit()).getTableName(),
				(new ReturnEx()).getTableName(),
		};
		
		String where = " where created >= " + Long.toString(start.getTime()) + " and created < " + Long.toString(finish.getTime());
		for(String table : tables) {
			String sql = "select distinct id from [" + table + "] "+ where;
			try {
				SQLiteCursor c = (SQLiteCursor) DataBaseManager.getDataBase().rawQuery(sql, null);
				while(c.moveToNext()) {
					todayOrgs.add(c.getString(0));
				}
				c.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
		super.onResume();
	}
	
	@Override
	protected void openMap(Org o) {
//		OrgLocationImpl oi = new OrgLocationImpl();
//		OrgLocation ol = oi.getData();
//		ol.id = o.id;
//		boolean haveLoc = oi.read();
//		oi.close();
		boolean haveLoc = o.latitude !=0 || o.longitude != 0;
		if(haveLoc) {
			try {
				String uri = String.format("geo:0,0?q=%s,%s", 
						Util.IntToScaleStr(o.latitude, Consts.GPS_SCALE, ".", false), 
						Util.IntToScaleStr(o.longitude, Consts.GPS_SCALE, ".", false),
						o.name);
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
				startActivity(intent);
			} catch (Exception e) {
				e.printStackTrace();
				super.openMap(o);
			}			
		} else
			super.openMap(o);
	}

	@Override
	protected void setOrgBackground(int pos, Org org, View v) {
		if( org == null || !debtOrgs.contains(org.id) ) {
			super.setOrgBackground(pos, org, v);
			return;
		}
		
		v.setBackgroundResource(R.drawable.red_row);
	}
}
