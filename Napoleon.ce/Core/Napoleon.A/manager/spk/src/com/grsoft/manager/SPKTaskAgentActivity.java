package com.grsoft.manager;

import com.grsoft.manager.spk.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.SPKTask;
import com.grsoft.dataobjects.impl.ManagerAgentImpl;
import com.grsoft.dataobjects.impl.SPKTaskImpl;
import com.grsoft.manager.documents.SPKTaskDoc;
import com.grsoft.network.ObjectListener;
import com.grsoft.util.Util;
import com.grsoft.util.gps.GPSUtilNew;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

public class SPKTaskAgentActivity extends Activity implements OnClickListener, OnItemClickListener, UpdateCtrl {
	private TextView tvStart;
	private TextView tvFinish;
	private TextView tvName;
	private Date start;
	private Date finish;
	private String agentid = "";
	private SPKTaskAgentAdapter adapter;
	private ListView list;
	
	private static final String AGENT_ID = "agent_id";
	
	public static void open(Context context, String id) {
		Intent i = new Intent(context, SPKTaskAgentActivity.class);
		i.putExtra(AGENT_ID, id);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.task_agent_view);
		
		tvStart = (TextView) findViewById(R.id.tvStart);
		tvFinish = (TextView) findViewById(R.id.tvFinish);
		tvName = (TextView) findViewById(R.id.tvName);
		list = (ListView) findViewById(R.id.list);
		
		agentid = getIntent().getStringExtra(AGENT_ID);
		
		tvStart.setOnClickListener(this);
		tvFinish.setOnClickListener(this);
		
		View v = getLayoutInflater().inflate(getActionBarLayoutID(), null);
        ActionBar a = getActionBar();
        a.setCustomView(v);
        a.setDisplayShowCustomEnabled(true);
        a.setDisplayShowTitleEnabled(false);
        
        TextView tv = (TextView) v.findViewById(R.id.tvTitle);
        tv.setText(R.string.tasks);
        
        Calendar c = Calendar.getInstance();
        c.setTime(Util.getDate());
        finish = c.getTime();
        c.add(Calendar.MONTH, -1);
        start = c.getTime();
        
        setTime(start, tvStart);
        setTime(finish, tvFinish);

        ManagerAgentImpl m = new ManagerAgentImpl();
        m.read("id", agentid);
        
        tvName.setText(m.getData().name);
        
        adapter = new SPKTaskAgentAdapter(this);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
	}

	private void setTime(Date date, TextView textView) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy", Locale.getDefault());
		StringBuilder sb = new StringBuilder();
		sb.append("<u>").append(sdf.format(date)).append("</u>");
		textView.setText(Html.fromHtml(sb.toString()));
	}

	private int getActionBarLayoutID() {
		return R.layout.action_bar;
	}
	
	DatePickerDialog.OnDateSetListener setStart = new DatePickerDialog.OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			start = new Date(year - 1900, monthOfYear, dayOfMonth);
			setTime(start, tvStart);
		}
	};
	
	DatePickerDialog.OnDateSetListener setFinish = new DatePickerDialog.OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			finish = new Date(year - 1900, monthOfYear, dayOfMonth);
			setTime(finish, tvFinish);
		}
	};

	private void showCalendar(Date date, DatePickerDialog.OnDateSetListener  result) {
		DatePickerDialog dlg = new DatePickerDialog(this, result, date.getYear() + 1900, 
				date.getMonth(), date.getDate());
    	dlg.show();
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.tvStart) 
			showCalendar(start, setStart);
		else if (v.getId() == R.id.tvFinish)
			showCalendar(finish, setFinish);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.spktask_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.itAdd) {
			addTask();
			return true;
		}else if (item.getItemId() == R.id.itSend) {
			send();
			return true;
		}else
			return super.onOptionsItemSelected(item);
	}

	void send() {
		List<ObjectListener> toSend = new ArrayList<ObjectListener>();
		toSend.add(SPKTaskDoc.instance().getDirtyDocuments());
		UpdateProcess up = new UpdateProcess(this, this, new ArrayList<Hitching>());
		up.setSending(toSend);
		
		up.execute((Void[])null);		
	}

	private void addTask() {
		SPKTaskImpl task = new SPKTaskImpl();
		if (task.init(this, start, finish, agentid, GPSUtilNew.getLastKnownLocation()));
			task.open(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		adapter.reload(agentid, start, finish);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		SPKTask t = (SPKTask) parent.getItemAtPosition(position);
		SPKTaskEdit.open(this, t.created.getTime());
	}

	@Override
	public void updateCtrl(boolean enabled) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFinish(boolean success) {
		adapter.reload(agentid, start, finish);
		adapter.notifyDataSetChanged();
	}
}
