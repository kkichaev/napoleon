package com.grsoft.napoleon;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.AgentTask;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.TaskCategory;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.util.gps.GPSUtilNew;
import com.grsoft.view.BaseActivity;

public class AgentTaskList extends BaseActivity {
	
	public String orgId;
	boolean canCheck;
	Adapter adapter;
	DbWriter wr = new DbWriter();
	
	public static void open(Context context, String orgId, boolean canCheck) {
		Intent i = new Intent(context, AgentTaskList.class);
		i.putExtra(ExtrasConst.ORG_ID_STR, orgId);
		i.putExtra(ExtrasConst.EDIT_MODE_STR, canCheck);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.agent_task_list);
		
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		orgId = b.getString(ExtrasConst.ORG_ID_STR);
		canCheck = b.getBoolean(ExtrasConst.EDIT_MODE_STR, false);
		
		ListView lv = (ListView)findViewById(R.id.lvItems);
		adapter = new Adapter();
		lv.setAdapter(adapter);
		adapter.refreshData(TaskCategory.ALL_CATEGORY);
		if( canCheck )
			lv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					AgentTask at = (AgentTask)arg0.getAdapter().getItem(arg2);
					at.SetDone(!at.IsDone());
					wr.insertRecord(at);
					adapter.notifyDataSetChanged();
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {}
			});
		
		Spinner s = (Spinner)findViewById(R.id.spCategory);
		TaskCategory.loadSpinner(s, true, null);
		s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				TaskCategory tc = (TaskCategory) arg0.getAdapter().getItem(arg2);
				adapter.refreshData(tc.name);
			}

			@Override public void onNothingSelected(AdapterView<?> arg0) {}
		});
		
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		wr.close();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(ExtrasConst.ORG_ID_STR, orgId);
		outState.putBoolean(ExtrasConst.EDIT_MODE_STR, canCheck);
	}
	
	class Adapter extends BaseAdapter {
		
		ArrayList<AgentTask> task = new ArrayList<AgentTask>();

		@Override public int getCount() { return task.size(); }

		@Override public Object getItem(int position) { return task.get(position); }

		@Override public long getItemId(int position) { return position; }

		public void refreshData(String name) {
			task.clear();
			
			AgentTask t = new AgentTask();
			DbReader r = new DbReader();
			String table = DataObjectInfo.getInstance().getTableName(t.getClass());
			String where = "id='" + orgId + "'";
			if( !TaskCategory.ALL_CATEGORY.equals(name) ) {
				where += " and category='" + name + "'";
			}
			boolean bdo = r.select(t, table, where, "appointDate");
			while(bdo) {
				task.add(t);
				t = new AgentTask();
				bdo = r.selectNext(t);
			}
			
			notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if( view == null )
				view = View.inflate(AgentTaskList.this, R.layout.agent_task_row, null);
			
			AgentTask t = (AgentTask) getItem(position);
			CheckBox cb = (CheckBox)view.findViewById(R.id.cbDone);
			if(canCheck) {
				cb.setVisibility(View.VISIBLE);
				cb.setChecked(t.IsDone());
			} else
				cb.setVisibility(View.GONE);
			TextView tv;
			tv = (TextView)view.findViewById(R.id.tvDate);
			tv.setText(Util.simpleDateFormat.format(t.appointDate));
			
			tv = (TextView)view.findViewById(R.id.tvTask);
			tv.setText(t.text);
			
			return view;
		}		
	}
	
	@Override
	public void onBackPressed() {
		if( !canCheck ) {
			OrderImpl oi = new OrderImpl();
			oi.init(this, orgId, GPSUtilNew.getLastKnownLocation());
		}
		super.onBackPressed();
	}
}
