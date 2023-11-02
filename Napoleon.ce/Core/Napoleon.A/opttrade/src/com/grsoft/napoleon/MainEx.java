package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Report;
import com.grsoft.util.MenuHandler;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MainEx extends Main {
	
	ReportList adapter = new ReportList();

	@Override
	protected ArrayList<MenuHandler> createDocMenuList() {
		ArrayList<MenuHandler> ret = super.createDocMenuList();
		
		ret.add(new MenuHandler(getString(R.string.reports), new Runnable() {
			@Override public void run() { showDialog(R.id.report_list_dlg);; }
		}));
		return ret;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == R.id.report_list_dlg ) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("ֲûבונטעו מעקוע");
			b.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Report r = (Report) adapter.getItem(which);
					if( r != null )
						ReportWebView.open(MainEx.this, r.name);
				}
			});
			return b.create();
		}
		
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if( id == R.id.report_list_dlg) {
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
				v = View.inflate(MainEx.this, R.layout.report_row, null);
			TextView tv = (TextView)v.findViewById(R.id.tvName);
			Report r = (Report)getItem(arg0);
			if( r != null )
				tv.setText(r.name);
			return v;
		}
		
	}

}
