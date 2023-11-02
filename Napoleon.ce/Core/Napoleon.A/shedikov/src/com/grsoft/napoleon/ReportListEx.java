package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.Locale;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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
import com.grsoft.dataobjects.impl.ReportsRequestImpl;
import com.grsoft.util.DataBaseAdapter;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.ReportSync;

public class ReportListEx extends FragmentActivity implements DataSetNotify {
	public static final String REFRESH_ACTION = "com.grsoft.napoleon.ReportList.REFRESH_ACTION";
	private ListView lvList;
	
	public static void open(Context context){
		Intent intent = new Intent(context, ReportListEx.class);
		context.startActivity(intent);
	}
	
	BroadcastReceiver refresh = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			((BaseAdapter)lvList.getAdapter()).notifyDataSetChanged();
		}
	};
	
	protected void onStart() {
		super.onStart();
		registerReceiver(refresh, new IntentFilter(REFRESH_ACTION));
	};
	
	
	
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
					ReportProp.show(ReportListEx.this, reportImpl.getData().id);
				else
					ReportWebView.open(ReportListEx.this, name);
			}
		});
		lvList.setDividerHeight(0);
		
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
		
		if(isFinishing())
			try{
				unregisterReceiver(refresh);
			}catch(Exception e){
				e.printStackTrace();
			}
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
					ReportProp.show(ReportListEx.this, reportImpl.getData().id);
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
	private ReportsRequestImpl rri = new ReportsRequestImpl();
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
	
	public ReportAdapter(Context context) throws IllegalAccessException, InstantiationException {
		super(context, new ReportDefImpl());
	}
	
	View.OnClickListener editRepProp = new View.OnClickListener() {
		
		@Override
		public void onClick(View view) {
			String id = (String)view.getTag();
			if( id != null )
				ReportProp.show((FragmentActivity)context, id);
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
				
				tv.setText(sdf.format(report.rcvdDate));
			}
			repi.close();
			
			rri.getData().id = rd.id;
			TextView tv = (TextView) convertView.findViewById(R.id.tvDateRequest);
			tv.setText("");
			
			if(rri.read())
				tv.setText(sdf.format(rri.getData().created));
			
			View v = convertView.findViewById(R.id.ivProps);
			if( v != null ) {
				v.setTag(rd.id);
				v.setOnClickListener(editRepProp);
			}
			
			convertView.setBackgroundResource(position % 2 != 0 ?	R.drawable.even_row_selector : R.drawable.list_selector);
		}
		
		return convertView;
	}
}
