package com.grsoft.napoleon;

import java.util.Date;

import com.grsoft.dataobjects.GwinnerAgentTask;
import com.grsoft.dataobjects.impl.GwinnerAgentTaskImpl;
import com.grsoft.napoleon.documents.GwinnerAgentTaskDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class GwinnerTaskEdit extends Activity {
	
	protected static final int DIALOG_DATE_PICKER_ID = 0;
	GwinnerAgentTaskImpl task = new GwinnerAgentTaskImpl();
	
	public static void open(Context context, GwinnerAgentTaskImpl task) {
		Intent i = new Intent(context, GwinnerTaskEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, task.getRowid());
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gw_task_edit);
		
		Bundle b = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
		long rid = b.getLong(ExtrasConst.DOC_ROW_ID_STR);
		task.read(rid);
		
		GwinnerAgentTask t = task.getData();
		boolean isCompleete = task.isComplete();
		boolean isEditable = task.isEditable();
		
		if(isEditable) {
			findViewById(R.id.tvTaskDate).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent i = new Intent(GwinnerTaskEdit.this, CalendarActivity.class);
					i.putExtra(ExtrasConst.DATE_TAG, task.getDate().getTime());
					startActivityForResult(i, DIALOG_DATE_PICKER_ID);
				}
			});
		}
		refreshDate();
		
		EditText ed = (EditText)findViewById(R.id.edText);
		ed.setText(t.task);
		ed.setEnabled(isEditable);
		
		CheckBox cb = (CheckBox)findViewById(R.id.cbDone);
		cb.setChecked(isCompleete);
		
		View btnOK = findViewById(R.id.btnOK);
		if( task.isEditable() || !isCompleete )
			btnOK.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) { 
					save();
					finish();
				}
			});
		
		else
			btnOK.setVisibility(View.GONE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( data != null && requestCode == DIALOG_DATE_PICKER_ID ) {
			Date curDate = new Date();
			long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
			Date newDate = new Date(ct);
			task.getData().date = newDate;
			refreshDate();
		}
	}	
	
	protected void save() {
		GwinnerAgentTask t = task.getData();
		EditText ed = (EditText)findViewById(R.id.edText);
		t.task = ed.getText().toString();
		
		CheckBox cb = (CheckBox)findViewById(R.id.cbDone);
		if( cb.isChecked()) {
			t.done = new Date();
			t.isComplete = 1;
		} else {
			t.done = t.created;
			t.isComplete = 0;
		}
		 
		// write internal in setExported 
		task.setExported(false);
		
		GwinnerAgentTaskDoc.instance().refreshDocSum(task.getId());
	}

	private void refreshDate() {
		TextView tv = (TextView)findViewById(R.id.tvTaskDate);
		
		String text = "Задача на <u><font color='blue'>" + Util.simpleDateFormat.format(task.getData().date) + "</font></u>";
		tv.setText(Html.fromHtml(text));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, task.getRowid());
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		task.close();
	}
}
