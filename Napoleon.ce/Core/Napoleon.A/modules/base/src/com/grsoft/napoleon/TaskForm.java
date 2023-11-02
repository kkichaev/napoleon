package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.database.TaskSendHitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Task;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.LoginData;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.ObjectExportListener;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.SendProgressManager;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.network.UserInfo;
import com.grsoft.network.WriteService;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Util;
import com.grsoft.view.RegDurationActivity;
import com.grsoft.view.TimerMessageBox;

public class TaskForm extends RegDurationActivity{
	
	private String orgId;
	private ArrayList<Task> tasks;
	private Task curSelected = null;
	private ImageButton btnSend;

	public static void open(String orgId, Context context) {
		Intent i = new Intent(context, TaskForm.class);
		i.putExtra(ExtrasConst.ORG_ID_STR, orgId);
		
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.task_form);
		
		Bundle config = ( savedInstanceState == null ) ? getIntent().getExtras() : savedInstanceState;
		orgId = config.getString(ExtrasConst.ORG_ID_STR);
		
		tasks = loadTask();
		
		btnSend = (ImageButton) findViewById(R.id.btnSend);
		btnSend.setOnClickListener(new OnClickListenerToNotify() {			
			@Override
			public void onClick(View v) {
				super.onClick(v);
				send();
			}
		});
		
		ListView task = (ListView)findViewById(R.id.lvTask);
		
		task.setAdapter(new TaskList());
		task.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if( position < tasks.size() ) {
					Task t = tasks.get(position);
					onTaskSelect(t);
				}
			}
		});
		
		if(tasks.size() > 0) {
			task.setSelection(0);
			onTaskSelect(tasks.get(0));
		}
	}
	
	protected void send() {
		if( curSelected != null ) {
			refreshTask(curSelected);
			curSelected = null;
			new TaskSender().execute((Void[])null);
		}
	}

	class TaskSender extends NetworkAsyncTask{
		private int traffic = 0;
		
		public TaskSender() {
			super(new SendProgressManager(TaskForm.this, btnSend));
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			TaskSendHitching taskSendHitching = new TaskSendHitching(orgId);
			
			if (taskSendHitching.size() == 0)
				return true;
			
			onUpdate(UpdateStatus.START_OF_PROCESS, 0);

			try	{
				CfgNplW config = (CfgNplW) ConfigManager.getConfig();
				UserInfo userInfo = new LoginData(config.login, config.passw, config.impersonate, TaskForm.this);
				List<ObjectExportListener> export = new ArrayList<ObjectExportListener>();
				export.add(taskSendHitching);
				WriteService writeService = (WriteService) RWServiceFactory
						.instance.createWriteService(export);
				writeService.setUpdateProcessListenet(this);
				
				if (!writeService.write(TaskForm.this, userInfo)){
					onUpdate(UpdateStatus.END_OF_PROCESS, 0);
					showErrorMsg(writeService.getMessage(), TaskForm.this);
					
					return false;
				}
				else{
					traffic += writeService.getSendedBytes();
					onUpdate(UpdateStatus.END_OF_PROCESS, 0);
					onUpdateMessage(new TimerMessageBox(getString(R.string.inform), 
						getString(R.string.sync_end_traffic) + 
						Integer.toString((traffic + 512) / 1024) + getString(R.string.kB), 
						TaskForm.this));
					
					return true;
				}
			} catch(Exception exception){
				onUpdate(UpdateStatus.END_OF_PROCESS, 0);
				showErrorMsg(exception.getMessage(), TaskForm.this);
				exception.printStackTrace();
				
				return false;
			} 
		}
		
		@Override
		protected void onPreExecute()
		{
			btnSend.setEnabled(true);
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			finish();
		}
	}

	@Override
	protected void onStop() {
		if( curSelected != null ) 
			refreshTask(curSelected);
		super.onStop();
	}
	
	private void onTaskSelect(Task t) {
		if( curSelected != null ) 
			refreshTask(curSelected);
		
		TextView tv = (TextView)findViewById(R.id.tvTask);
		tv.setText(t.task);
		
		EditText ed = (EditText)findViewById(R.id.tvDoing);
		ed.setText(t.done);
		
		curSelected = t;
	}
	
	private void refreshTask(Task task) {
		EditText ed = (EditText)findViewById(R.id.tvDoing);
		String text = ed.getText().toString();
		
		if (text.trim().length() > 0){
			task.done = text;
			task.dodate = Util.getDateTime();
		
			DbWriter writer = new DbWriter();
			writer.insertRecord(task);
			writer.close();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(ExtrasConst.ORG_ID_STR, orgId);
	}
	
	private ArrayList<Task> loadTask() {
		ArrayList<Task> res = new ArrayList<Task>();
		
		String where = String.format("id = '%s'", orgId);
		
		DbReader reader = new DbReader();
		Task t = new Task();
		String table = DataObjectInfo.getInstance().getTableName(Task.class);
		boolean bdo = reader.select(t, table, where, "created");
		while( bdo ) {
			res.add(t);
			t = new Task();			
			bdo = reader.selectNext(t);
		}
		reader.close();
		
		return res;
	}
	
	class TaskList extends BaseAdapter {

		@Override public int getCount() { return tasks.size(); }

		@Override public Object getItem(int arg0) { return (arg0 < tasks.size()) ? tasks.get(arg0) : null; }

		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if( convertView == null )
				convertView = View.inflate(TaskForm.this, R.layout.task_row, null);
			
			convertView.setBackgroundResource(
					position % 2 != 0 ? R.drawable.even_row_selector:  
									R.drawable.list_selector);
			
			Task t = (Task)getItem(position);
			if( t != null ) {
				SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy");
				TextView tv = (TextView)convertView.findViewById(R.id.tvTask);
				
				if (t.done.trim().length() > 0)
					tv.setTextColor(Color.GRAY);
				
				tv.setText(t.task);
				
				tv = (TextView)convertView.findViewById(R.id.tvDate);
				tv.setText(sf.format(t.created));
			}
			
			return convertView;
		}
		
	}
}
