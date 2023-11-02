package com.grsoft.napoleon;

import java.util.Date;
import java.util.HashSet;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgLocation;
import com.grsoft.dataobjects.impl.OrgLocationImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

public class MainEx extends Main {
	public static HashSet<String> debtOrgs = null;
	
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
	protected void onResume() {
		Date d = new Date();
		debtOrgs = new HashSet<String>();
		loadSet(debtOrgs, d);
		
		super.onResume();
	}
	
	@Override
	protected void openMap(Org o) {
		OrgLocationImpl oi = new OrgLocationImpl();
		OrgLocation ol = oi.getData();
		ol.id = o.id;
		boolean haveLoc = oi.read();
		oi.close();
		if(haveLoc) {
			try {
				String uri = String.format("geo:0,0?q=%s,%s", 
						Util.IntToScaleStr(ol.latitude, Consts.GPS_SCALE, ".", false), 
						Util.IntToScaleStr(ol.longitude, Consts.GPS_SCALE, ".", false),
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
