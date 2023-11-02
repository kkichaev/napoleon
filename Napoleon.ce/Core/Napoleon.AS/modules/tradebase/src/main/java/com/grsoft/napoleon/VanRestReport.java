package com.grsoft.napoleon;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.modules.print.util.VanRestData;
import com.grsoft.napoleon.printsources.VanRestSource;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

public class VanRestReport extends BaseActivity {
	protected static final int WAIT_FOR_PRINT_DLG = 1;
	public static Class<? extends Activity> activity = VanRestReport.class;
	ArrayList<VanRestData> data = new ArrayList<VanRestData>();
	
	public static void open(Context c) {
		Intent i = new Intent(c, activity);
		c.startActivity(i);
	}

	protected int getContentID() { return R.layout.van_rest_report; }  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getContentID());
		buildData();
		
		View prn = findViewById(R.id.btnPrint);
		if( prn != null ) {
			prn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					try{
						printing();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			});
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case WAIT_FOR_PRINT_DLG:
			return SelectPrinFormDlg.createWaitDlg(this);
		default:
			return super.onCreateDialog(id);
		}
	}
	
	
	protected void buildData() {
		String table = DataObjectInfo.getInstance().getTableName(Price.class);
		String sql = "SELECT name, vanQty, weight FROM " + table + " where vanQty>0 ORDER BY name";
		
		try {
			SQLiteCursor c = (SQLiteCursor) DataBaseManager.getDataBase().rawQuery(sql, null);
			while(c.moveToNext()) {
				VanRestData d = new VanRestData();
				d.name = c.getString(0);
				d.qty = c.getInt(1);
				d.weight = (int)(c.getLong(2) * d.qty / Consts.QTY_SCALE);
				
				data.add(d);
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.setAdapter(new RestAdapter());
	}
	
	protected void printing() {
		VanRestSource vanrest = new VanRestSource(data);
		SelectPrinFormDlg.createPrintForm(VanRestReport.this, vanrest, WAIT_FOR_PRINT_DLG, "vanrest",null);
	}

	class RestAdapter extends BaseAdapter {

		@Override public int getCount() { return data.size(); }

		@Override public Object getItem(int position) {
			return position < data.size() ? data.get(position) : null;
		}

		@Override public long getItemId(int position) { return position; }

		protected void setQty(TextView tv, VanRestData d) {
			String text = Util.IntToScaleStr(d.qty, Consts.QTY_SCALE);
			tv.setText(text);
		}
		
		@Override
		public View getView(int position, View v, ViewGroup parent) {
			if( v == null )
				v = View.inflate(VanRestReport.this, R.layout.van_rest_row, null);
			
			VanRestData d = (VanRestData) getItem(position);
			
			if( d != null ) {
				TextView tv;
				
				tv = (TextView)v.findViewById(R.id.tvName);
				tv.setText(d.name);

				tv = (TextView)v.findViewById(R.id.tvQty);
				setQty(tv, d);
			}
			return v;
		}
		
	}
}