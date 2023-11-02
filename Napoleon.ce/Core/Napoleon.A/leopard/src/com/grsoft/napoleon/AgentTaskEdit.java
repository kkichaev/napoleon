package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.AgentTask;
import com.grsoft.dataobjects.TaskCategory;
import com.grsoft.dataobjects.impl.AgentTaskImpl;
import com.grsoft.dataobjects.impl.Cursor;
import com.grsoft.dataobjects.impl.TaskCategoryImpl;
import com.grsoft.script.dataobjects.impl.ScriptImplEx;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

public class AgentTaskEdit extends Activity {
	private List<EditText> controls = new ArrayList<EditText>();
	private final static String DATE = "date";
	private long date;
	private Date appointDate;
	private TextView tvDate;
	
	public static void open(Context context, long date){
		Intent intent = new Intent(context, AgentTaskEdit.class);
		intent.putExtra(DATE, date);
		context.startActivity(intent);
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.agenttaskedit);
		
		TableLayout table = (TableLayout) findViewById(R.id.table);
		table.setBackgroundColor(Color.WHITE);
		Cursor<TaskCategory> c = new Cursor<TaskCategory>(new TaskCategoryImpl());
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.WEEK_OF_YEAR, 1);
		tvDate = (TextView) findViewById(R.id.tvDate);
		appointDate = calendar.getTime();
		updateDateView();
		
		Intent intent = getIntent();
		if(intent != null)
			date = intent.getLongExtra(DATE, -1);
		
		while(c.moveNext()){
			TableRow tr = new TableRow(this);
			TableRow.LayoutParams lp = new TableRow.LayoutParams(
					TableRow.LayoutParams.FILL_PARENT,
					TableRow.LayoutParams.WRAP_CONTENT);
			tr.setLayoutParams(lp);
			
			TextView textView = new TextView(this);
			textView.setLayoutParams(lp);
			textView.setText(c.current().getData().name);
			textView.setTextColor(Color.BLACK);
			textView.setTextSize(16);
			
			tr.addView(textView);
			table.addView(tr, 
					new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, 
							TableLayout.LayoutParams.WRAP_CONTENT));

			tr = new TableRow(this);
			EditText editText = new EditText(this);
			editText.setLayoutParams(new TableRow.LayoutParams(
					TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT, (float)1.0));
			editText.setTag(c.current().getData().name);
			controls.add(editText);
			tr.addView(editText);
			
			table.addView(tr, 
					new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, 
							TableLayout.LayoutParams.WRAP_CONTENT));
		}
		
		c.close();
		
		LinearLayout lldate = (LinearLayout) findViewById(R.id.lldate);
		lldate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), 
						CalendarActivity.class);
				i.putExtra(ExtrasConst.DATE_TAG, appointDate.getTime());
				startActivityForResult(i, 0);
			}
		});
	}

	private void updateDateView() {
		tvDate.setText(Util.simpleDateFormat.format(appointDate));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, 
				Calendar.getInstance().getTime().getTime());
		appointDate = new Date(ct);
		updateDateView();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			ArrayList<AgentTaskImpl> list = new ArrayList<AgentTaskImpl>();
			SharedPreferences pref = getSharedPreferences(ScriptImplEx.SCRIPT_PREF, Context.MODE_PRIVATE);
			Date scriptData = new Date(pref.getLong(ScriptImplEx.SCRIPT_DATE, new Date().getTime()));
			String id = pref.getString(ScriptImplEx.SCRIPT_ID, "");
			
			for(EditText ed : controls){
				if(ed.getText().toString().trim().length() > 0){
					AgentTaskImpl impl = new AgentTaskImpl();
					AgentTask task = impl.getData();
					task.created = Calendar.getInstance().getTime();
					
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					task.date = scriptData;
					task.id = id;
					task.appointDate = appointDate;
					task.script = new Date(date);
					task.execDate = new Date(0);
					task.category = (String) ed.getTag();
					task.text = ed.getText().toString().trim();
					list.add(impl);
				}else
					break;
			}
			
			if(list.size() == controls.size()){
				for(AgentTaskImpl i: list){
					i.write();
					i.close();
				}
				
				finish();
			}else
				Toast.makeText(this, "Заполните все поля", Toast.LENGTH_LONG).show();
			
			return true;
		}else
			return super.onKeyDown(keyCode, event);
	}
}
