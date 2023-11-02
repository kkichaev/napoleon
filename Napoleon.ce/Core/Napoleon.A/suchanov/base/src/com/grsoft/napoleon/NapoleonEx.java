package com.grsoft.napoleon;

import java.util.Date;
import java.util.HashSet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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

public class NapoleonEx extends Main {
	static final int ORG_INFO = 1000;
	
	boolean loadedDebs = false;
	private Org infoOrg = null;
	private HashSet<String> outOrgs = new HashSet<String>();

	@Override
	protected void onResume() {
		loadedDebs = false;
		super.onResume();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == ORG_INFO )
			return createInfoDialog();
		
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		if( id == ORG_INFO )
			setInfo((AlertDialog)dialog);
	}
	
	private boolean isOutOrg(Org o) {
		if( !loadedDebs ) {
			loadedDebs = true;
			
			outOrgs.clear();
			
			SQLiteDatabase db = DataBaseManager.getDataBase();
			
			String table = DataObjectInfo.getInstance().getTableName(Delivery.class);			
			String sql = "SELECT id FROM " + table + " WHERE paydate < ? GROUP BY id";
			
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
		
		return outOrgs.contains(o.id);
	}
	
	@Override
	public void openOrg(Org org) {
		if( ((OrgEx)org).info.length() > 0 ) {
			infoOrg = org;
			showDialog(ORG_INFO);
		} else
			super.openOrg(org);
	}
	
	@Override
	protected void drawOrg(Org oi, View view) {
		super.drawOrg(oi, view);

		if( isOutOrg(oi) ) {
			TextView tv = (TextView)view.findViewById(R.id.tvOrgName);
			tv.setTextColor(Color.RED);			
		}
	}

	private void setInfo(AlertDialog dialog) {
		if( infoOrg == null )
			return;
		
		dialog.setMessage(((OrgEx)infoOrg).info);
	}
	
	private Dialog createInfoDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Информация");
		builder.setMessage("");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				NapoleonEx.super.openOrg(infoOrg);
			}			
		});
		return builder.create();
	}
}
