package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.dataobjects.Report;
import com.grsoft.dataobjects.ReportDef;
import com.grsoft.dataobjects.impl.ReportDefImpl;
import com.grsoft.dataobjects.impl.ReportImpl;
import com.grsoft.util.DataBaseAdapter;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.ReportSync;
import com.grsoft.view.BaseActivity;

public class TGReportList extends BaseActivity implements DataSetNotify {
	private ListView lvList;
	
	public static void open(Context context){
		Intent intent = new Intent(context, TGReportList.class);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reports);
		
		lvList = (ListView)findViewById(R.id.list);
		
		try{
			lvList.setAdapter(new ReportAdapter(this));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		registerForContextMenu(lvList);
		
		lvList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View arg1, int pos,
					long arg3) {
				ReportDefImpl reportImpl = (ReportDefImpl) adapterView.getItemAtPosition(pos);
				
				String name = reportImpl.getData().id;
				if (ReportImpl.haveReport(name) == false)
					ReportParamEdit.open(TGReportList.this, name);
				else
					ReportWebView.open(TGReportList.this, name);
			}
		});
		
		findViewById(R.id.btnSync).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { syncReports(); }
		});
	}
	
	protected void syncReports() {
		ReportSync rs = new ReportSync(this, findViewById(R.id.btnSync));
		rs.execute((Void[])null);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Adapter adapter = lvList.getAdapter();
		
		if (adapter != null)
			((BaseAdapter)adapter).notifyDataSetChanged();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		Adapter adapter = lvList.getAdapter();
		
		if (adapter != null)
			((DataBaseAdapter<?>)adapter).close();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.reports_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		if (item.getItemId() == R.id.itQuery){
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			
			if (info != null){
				DataBaseAdapter<?> adapter = (DataBaseAdapter<?>) lvList.getAdapter();
				
				if (adapter != null){
					ReportDefImpl reportImpl =  (ReportDefImpl)adapter.getItem(info.position);
					ReportParamEdit.open(TGReportList.this, reportImpl.getData().id);
				}
			}
		} else
			return super.onContextItemSelected(item);
		
		return true;
	}

	@Override
	public void notifyDataSetChanged() {
		((ReportAdapter)lvList.getAdapter()).notifyDataSetChanged();
	}
}

class ReportAdapter extends DataBaseAdapter<ReportDef>{
	public ReportAdapter(Context context) throws IllegalAccessException, InstantiationException {
		super(context, new ReportDefImpl());
	}
	
	View.OnClickListener editRepProp = new View.OnClickListener() {
		
		@Override
		public void onClick(View view) {
			String id = (String)view.getTag();
			if( id != null )
				ReportParamEdit.open(context, id);
		}
	};

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = View.inflate(context, R.layout.reports_row, null);
		
		ReportDefImpl ri = (ReportDefImpl) getItem(position);
		
		if (ri != null && ri.read()){
			ReportDef rd = ri.getData();
			String name = rd.name;
			((TextView)convertView.findViewById(R.id.tvName)).setText(name);
			
			ReportImpl repi = new ReportImpl();
			Report report = repi.getData();
			report.id = rd.id;
			if( repi.read() ) {
				TextView tv = (TextView) convertView.findViewById(R.id.tvDate);
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
				tv.setText(sdf.format(report.rcvdDate));
			}
			repi.close();
			
			View v = convertView.findViewById(R.id.ivProps);
			if( v != null ) {
				v.setTag(rd.id);
				v.setOnClickListener(editRepProp);
			}
		}
		
		return convertView;
	}
}
