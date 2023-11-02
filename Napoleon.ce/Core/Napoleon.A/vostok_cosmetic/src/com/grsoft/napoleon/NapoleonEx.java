package com.grsoft.napoleon;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.impl.OrgImpl;

public class NapoleonEx extends Napoleon {

	HashSet<String> debtors = new HashSet<String>();
	
	@Override
	protected void onResume() {
		loadDebtors();

		super.onResume();
	}
	
	private void loadDebtors() {
		debtors.clear();

		String table = DataObjectInfo.getInstance().getTableName(DeliveryEx.class);
		if(DbWriter.isTableExists(table)) {
			Calendar c = Calendar.getInstance();
			c.add(Calendar.YEAR, -5);
			Date minDate = c.getTime();
			Date check = new Date();
			
			try {
				String sql = "SELECT DISTINCT id from " + table
						+ " WHERE sumD > 0 and date > "
						+ Long.toString(minDate.getTime()) + " and date < "
						+ Long.toString(check.getTime());
				SQLiteDatabase db = DataBaseManager.getDataBase();
				Cursor cr = db.rawQuery(sql, null);
				while (cr.moveToNext()) {
					debtors.add(cr.getString(0));
				}
				cr.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void drawOrg(OrgImpl oi, View view) {
		super.drawOrg(oi, view);
		
		if(debtors.contains(oi.getData().id) ) {
			int color = Color.RED;
			((TextView)view.findViewById(R.id.tvOrgName)).setTextColor(color);
			((TextView)view.findViewById(R.id.tvOrgSum)).setTextColor(color);
		}
//		if(debtors.contains(oi.getData().id) ) {
//			ViewGroup vg = (ViewGroup)view;
//			for( int i=0; i<vg.getChildCount(); i++ ) {
//				View v = vg.getChildAt(i);
//				if( v instanceof TextView )
//					((TextView)v).setTextColor(Color.RED);
//			}
//		}
	}
}
