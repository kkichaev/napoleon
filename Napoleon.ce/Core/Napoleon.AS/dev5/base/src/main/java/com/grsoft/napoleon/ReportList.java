package com.grsoft.napoleon;
import com.grsoft.aceteam.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.impl.ReportAnswerImpl;
import com.grsoft.network.ReportSync;
import com.grsoft.util.DataSetNotify;
import com.grsoft.view.BaseActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ReportList extends BaseActivity implements DataSetNotify {
	Adapter adapter;
	public static Class<? extends Activity> activity = ReportList.class;
	
	public static void open(Context context) {
		Intent i = new Intent(context, activity);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(getContentViewID());
		ListView lv = (ListView)findViewById(R.id.lvItems);
		adapter = new Adapter();
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				RowData rd = (RowData)arg0.getItemAtPosition(arg2);
				if(ReportAnswerImpl.haveData(rd.id))
					ReportView.open(ReportList.this, rd.id);
				else
					ReportParams.open(ReportList.this, rd.id);
			}
		});
		
		findViewById(R.id.btnSync).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { refreshReports(); }
		});
	}
	
	protected void refreshReports() {
		ReportSync rs = new ReportSync(this, findViewById(R.id.btnSync));
		rs.execute((Void[])null);
	}

	@Override public void notifyDataSetChanged() { adapter.refresh(); }

	View.OnClickListener openRequest = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			RowData rd = (RowData)arg0.getTag();
			if( rd != null && rd.id != null)
				ReportParams.open(ReportList.this, rd.id);
		}
	};
	
	protected int getContentViewID() { return R.layout.report_list; }
	
	@Override
	protected void onResume() {
		super.onResume();
		adapter.refresh();
	}
	
	class Adapter extends BaseAdapter {

		List<RowData> items = new ArrayList<ReportList.RowData>();
		
		public Adapter() {}
		
		public void refresh() {
			items.clear();
			
			SQLiteDatabase db = DataBaseManager.getDataBase();
			
			DataObjectInfo doi = DataObjectInfo.getInstance();
			
			DbWriter.checkDBTable(com.grsoft.dataobjects.ReportList.class);
			DbWriter.checkDBTable(com.grsoft.dataobjects.ReportAnswer.class);
			DbWriter.checkDBTable(com.grsoft.dataobjects.ReportRequest.class);
			
			String stmt = "select rl.id, rl.name, rr.sent as requestDate, ra.rcvdDate as reportDate from [" +
					doi.getTableName(com.grsoft.dataobjects.ReportList.class) + "] rl LEFT JOIN [" + 
					doi.getTableName(com.grsoft.dataobjects.ReportAnswer.class) + "] ra ON rl.id = ra.id LEFT JOIN [" +
					doi.getTableName(com.grsoft.dataobjects.ReportRequest.class) + "] rr ON rl.id = rr.id";
			
			Cursor c = null;
			try {
				c = db.rawQuery(stmt, null);
				while(c.moveToNext()) {
					int clmn = 0;
					RowData item = new RowData();
					item.id = c.getString(clmn++);
					item.name = c.getString(clmn++);
					if( !c.isNull(clmn) ) {
						long val = c.getLong(clmn);
						if( val != 0)
							item.requestDate = new Date(val);
					}
					clmn++;
					if( !c.isNull(clmn) )
						item.reportDate = new Date(c.getLong(clmn));
					
					items.add(item);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(c != null)
					c.close();
			}
			
			notifyDataSetChanged();
		}
		
		@Override public int getCount() { return items.size(); }
		@Override public Object getItem(int arg0) { return items.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			if(view == null)
				view = View.inflate(ReportList.this, R.layout.report_list_row, null);
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm:ss", Locale.getDefault());
			RowData rd = (RowData) getItem(arg0);

			TextView tv;
			tv = (TextView)view.findViewById(R.id.tvName);
			tv.setText(rd.name);
			
			tv = (TextView)view.findViewById(R.id.tvDate);
			String text = "";
			if( rd.reportDate != null)
				text = sdf.format(rd.reportDate);
			tv.setText(text);

			tv = (TextView)view.findViewById(R.id.tvReqDate);
			text = "";
			if( rd.requestDate != null)
				text = sdf.format(rd.requestDate);
			tv.setText(text);

			
			View props = view.findViewById(R.id.ivProps);
			props.setTag(rd);
			props.setOnClickListener(openRequest);
			return view;
		}
		
	}
	
	class RowData {
		public String name;
		public String id;
		public Date requestDate = null;
		public Date reportDate = null;
	}
}
