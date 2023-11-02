package com.grsoft.napoleon;

import java.util.Date;
import java.util.HashSet;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocType;

public class NapoleonEx extends Napoleon {
	boolean loadedDebs = false;
	private HashSet<String> outOrgs = new HashSet<String>();

	@Override
	protected void onResume() {
		loadedDebs = false;
		super.onResume();
	}
	
	private boolean isOutOrg(OrgImpl o) {
		if( !loadedDebs ) {
			loadedDebs = true;
			
			outOrgs.clear();
			
			SQLiteDatabase db = DataBaseManager.getDataBase();
			
			String table = DataObjectInfo.getInstance().getTableName(Delivery.class);			
			String sql = "SELECT id FROM " + table + " WHERE paydate < ? and sumD <> 0 GROUP BY id";
			
			Date curDate = new Date();
			String[] args = { Long.toString(curDate.getTime()) };
	
			try {
				Cursor c = db.rawQuery(sql, args);
				while( c.moveToNext() )
					outOrgs.add(c.getString(0));
				c.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return outOrgs.contains(o.getData().id);
	}
	
	@Override
	protected void drawOrg(OrgImpl oi, View view) {
		super.drawOrg(oi, view);

		if( DocType.getCurDoc().isHasCreatedToday(oi.getData().id) == false && isOutOrg(oi) ) {
			TextView tv = (TextView)view.findViewById(R.id.tvOrgName);
			tv.setTextColor(Color.RED);			
		}
	}
}
