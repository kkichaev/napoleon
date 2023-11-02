package com.grsoft.napoleon;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.impl.OrgImpl;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.AdapterView.OnItemClickListener;

public class NapoleonEx extends Napoleon {
	public static final int OUT_PAY_DIALOG = 0x1230;
	public static HashSet<String> debtOrgs = null;
	OrgClicked orgClicked;
	
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
		Calendar c = Calendar.getInstance(Locale.getDefault());
		c.add(Calendar.DAY_OF_MONTH, 3);
		debtOrgs = new HashSet<String>();
		loadSet(debtOrgs, c.getTime());

		super.onResume();
	}
	
	protected OnItemClickListener getItemOnClickListner() { 
		orgClicked = new OrgClicked();
		return orgClicked;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == OUT_PAY_DIALOG) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Предупреждение");
			b.setMessage("В ближайшее время клиент попадёт в стоп-лист");
			b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					orgClicked.resumeClick();
					dialog.dismiss();
				}
			});
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	class OrgClicked extends OrglListOnClickListener {
		@Override
		protected void openOrg(OrgImpl oi) {
			if( debtOrgs.contains(oi.getData().id) ){
				clickedOrg = oi;
				showDialog(OUT_PAY_DIALOG);
				return;
			}
			super.openOrg(oi);
		}
	}
}
