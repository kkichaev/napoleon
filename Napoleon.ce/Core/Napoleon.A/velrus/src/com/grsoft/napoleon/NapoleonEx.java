package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.Report;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.MenuHandler;

public class NapoleonEx extends Napoleon {
	public static HashSet<String> debtOrgs = null;
	private static final int GET_REPORT_LIST = R.id.get_report_list_dlg;
	ReportList adapter = new ReportList();
	
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
			v.setBackgroundResource(R.drawable.lgray_row);
		else  
			super.setOrgBackground(pos, org, v);
	}
	
	@Override
	protected ArrayList<MenuHandler> createDocMenuList() {
		ArrayList<MenuHandler> ret = super.createDocMenuList();
	
		ret.add(new MenuHandler(getString(R.string.reports), new Runnable() {
			@Override public void run() { showDialog(GET_REPORT_LIST); }
		}));
		return ret;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == GET_REPORT_LIST ) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("ֲûבונטעו מעקוע");
			b.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Report r = (Report) adapter.getItem(which);
					if( r != null ){
						ReportWebView.open(NapoleonEx.this, r.name);
						dialog.dismiss();
					}
				}
			});
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if( id == GET_REPORT_LIST) {
			AlertDialog ad = (AlertDialog)dialog;
			((ReportList)ad.getListView().getAdapter()).refresh();
		}
		super.onPrepareDialog(id, dialog);
	}

	class ReportList extends BaseAdapter {
		
		List<Report> reports = new ArrayList<Report>();

		public void refresh() {
			reports.clear();
			
			DataTraveler.travel(Report.class, new DataTraveler.Travel<Report>() {

				@Override
				public boolean travel(DataTraveler<Report> item) {
					reports.add(item.data);
					item.data = new Report();
					return true;
				}
			}, null);
			notifyDataSetChanged();
		}
		
		@Override
		public int getCount() {
			return reports.size();
		}

		@Override
		public Object getItem(int arg0) {
			return arg0 < getCount() ? reports.get(arg0) : null;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View v, ViewGroup arg2) {
			if( v == null )
				v = View.inflate(NapoleonEx.this, R.layout.report_row, null);
			TextView tv = (TextView)v.findViewById(R.id.tvName);
			Report r = (Report)getItem(arg0);
			if( r != null )
				tv.setText(r.name);
			return v;
		}
		
	}

}
