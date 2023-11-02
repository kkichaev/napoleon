package com.grsoft.napoleon;

import java.util.Date;
import java.util.HashSet;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DebetItem;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainEx extends Main {
	
	public static HashSet<String> debtOrgs = new HashSet<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		loadSet(new Date());
	}
	
	@Override
	protected void drawOrg(Org org, View view) {
		super.drawOrg(org, view);
		
		if (DocType.getCurDoc() == DebtDoc.instance() && org != null && !debtOrgs.contains(org.id) ) {
			((TextView)view.findViewById(R.id.tvOrgName)).setTextColor(Color.RED);
			return;
		}
	}
	
	public static void loadSet(Date checkDate) {
		debtOrgs.clear();
		
		SQLiteDatabase db = DataBaseManager.getDataBase();
		DbWriter.checkDBTable(Delivery.class);
		String table = DataObjectInfo.getInstance().getTableName(Delivery.class);			
		String sql = "SELECT DISTINCT id FROM " + table + " WHERE paydate <= ? and sumD > 0";		
		String[] args = { Long.toString(checkDate.getTime()) };
		try {
			Cursor c = db.rawQuery(sql, args);
			while( c.moveToNext() )
				debtOrgs.add(c.getString(0));
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
