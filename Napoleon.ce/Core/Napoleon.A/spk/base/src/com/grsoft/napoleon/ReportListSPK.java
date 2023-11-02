package com.grsoft.napoleon;

import java.util.Date;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.dataobjects.AgentCfgHitching;
import com.grsoft.dataobjects.Reports;
import com.grsoft.dataobjects.impl.ReportAnswerSPKImpl;
import com.grsoft.dataobjects.impl.ReportsImpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.DataBaseAdapter;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

public class ReportListSPK extends BaseActivity {
	private ListView lvList;
	
	public static void open(Context context){
		Intent intent = new Intent(context, ReportListSPK.class);
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
				ReportsImpl reportImpl = (ReportsImpl) adapterView
						.getItemAtPosition(pos);
				
				String name = reportImpl.getData().name;
				if (ReportAnswerSPKImpl.getAnswerDate(name) == null)
					ReportParamEdit.open(ReportListSPK.this, name);
				else
					ReportWebView.open(ReportListSPK.this, name);
			}
		});
		
		CheckBox cbAutoRequest = (CheckBox) findViewById(R.id.cbAutoRequest);
		Boolean aar = (Boolean)ConfigManager.getConfig()
				.getProperty(AgentCfgHitching.AUTOMATIC_REQUEST_REPORT);
		
		if (aar != null)
			cbAutoRequest.setChecked(aar);
		
		cbAutoRequest.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				ConfigManager.getConfig().setProperty(
						AgentCfgHitching.AUTOMATIC_REQUEST_REPORT, 
						isChecked);
				ConfigManager.save();
			}
		});
		cbAutoRequest.setVisibility(View.GONE);
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
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.reports_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		if (item.getItemId() == R.id.itQuery){
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			
			if (info != null){
				DataBaseAdapter<?> adapter = (DataBaseAdapter<?>) lvList.getAdapter();
				
				if (adapter != null){
					ReportsImpl reportImpl =  (ReportsImpl)adapter.getItem(info.position);
					ReportParamEdit.open(ReportListSPK.this, reportImpl.getData().name);
				}
			}
		} else
			return super.onContextItemSelected(item);
		
		return true;
	}
}

class ReportAdapter extends DataBaseAdapter<Reports>{
	public ReportAdapter(Context context)
			throws IllegalAccessException, InstantiationException {
		super(context, new ReportsImpl());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = View.inflate(context, R.layout.reports_row, null);
		
		ReportsImpl ri = (ReportsImpl) getItem(position);
		
		if (ri != null && ri.read()){
			String name = ri.getData().name;
			((TextView)convertView
					.findViewById(R.id.tvName))
					.setText(name);
			((TextView)convertView
					.findViewById(R.id.tvType))
					.setText(ri.getData().type);
			
			TextView tvDate = (TextView) convertView.findViewById(R.id.tvDate);
			Date date = ReportAnswerSPKImpl.getAnswerDate(name);
			
			if (date != null){
				tvDate.setText(Util.simpleDateFormat.format(date));
				tvDate.setVisibility(View.VISIBLE);
			}else
				tvDate.setVisibility(View.GONE);
			
		}
		
		return convertView;
	}
}
