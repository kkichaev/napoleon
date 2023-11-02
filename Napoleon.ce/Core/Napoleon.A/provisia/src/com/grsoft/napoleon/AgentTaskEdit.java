package com.grsoft.napoleon;

import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.dataobjects.AgentTask;
import com.grsoft.dataobjects.TaskCategory;
import com.grsoft.dataobjects.impl.AgentTaskImpl;
import com.grsoft.napoleon.documents.AgentTaskDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

public class AgentTaskEdit extends BaseActivity {
	
	AgentTaskImpl task = new AgentTaskImpl();
	
	public static void open(Context context, AgentTaskImpl task) {
		Intent i = new Intent(context, AgentTaskEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, task.getRowid());
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.agent_task_edit);
		
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		long rid = b.getLong(ExtrasConst.DOC_ROW_ID_STR);
		task.read(rid);
		
		AgentTask t = task.getData();
		
		TextView tv = (TextView)findViewById(R.id.tvDate);
		tv.setPaintFlags(tv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		tv.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(AgentTaskEdit.this, CalendarActivity.class);
				i.putExtra(ExtrasConst.DATE_TAG, task.getData().appointDate.getTime());
				startActivityForResult(i, 0);
			}
		});
		
		EditText ed = (EditText)findViewById(R.id.edTask);
		ed.setText(t.text);
		
		Spinner s = (Spinner)findViewById(R.id.spCategory);
		TaskCategory.loadSpinner(s, false, t.category);
		
		findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				save();
				finish();
			}
		});
		
		refreshDate();
	}
	
	private void refreshDate() {
		TextView tv = (TextView)findViewById(R.id.tvDate);
		tv.setText(Util.simpleDateFormat.format(task.getData().appointDate));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( resultCode == RESULT_OK ) {
			Date curDate = new Date();
			long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
			task.getData().appointDate = new Date(ct);
			
			refreshDate();
		}
	}
	
	protected void save() {
		if( task.isExported() )
			return;
		
		AgentTask t = task.getData();

		Spinner s = (Spinner)findViewById(R.id.spCategory);
		TaskCategory tc = (TaskCategory)s.getSelectedItem();
		
		EditText ed = (EditText)findViewById(R.id.edTask);
		
		t.category = tc.name;
		t.text = ed.getText().toString();
		
		task.write();
		
		AgentTaskDoc.instance().refreshDocSum(t.id);
	}

	@Override
	protected void onStop() {
		super.onStop();
		task.close();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, task.getRowid());
	}
}
