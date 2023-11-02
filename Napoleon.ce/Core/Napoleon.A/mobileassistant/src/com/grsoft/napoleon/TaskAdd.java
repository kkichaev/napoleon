package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.NapoleonTask;
import com.grsoft.dataobjects.impl.NapoleonTaskImpl;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

public class TaskAdd extends BaseActivity {

	private static final int DIALOG_DATE_PICKER_ID = 0;
	Date date;
	
	public static void open(Context c) {
		Intent i = new Intent(c, TaskAdd.class);
		c.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_add);
	
		date = Util.getDate();

		findViewById(R.id.tvDate).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(TaskAdd.this, CalendarActivity.class);
				i.putExtra(ExtrasConst.DATE_TAG, date.getTime());
				startActivityForResult(i, DIALOG_DATE_PICKER_ID);
			}
		});

		findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { finish(); }
		});
		
		findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
			@Override 
			public void onClick(View v) {
				String task = ((EditText)findViewById(R.id.edText)).getText().toString();
				if(task.length() > 0 ) {
					NapoleonTaskImpl nti = new NapoleonTaskImpl();
					NapoleonTask nt = nti.getData();
					
					nt.id = UUID.randomUUID().toString().replace("-'", "");
					nt.start = date;
					nt.end = date;
					nt.task = task;
					nt.params = NapoleonTask.CREATED;
					
					AgentPrefix ap = AgentPrefix.get();
					if( ap != null )
						nt.userid = ap.id;
					
					nti.write();
					nti.close();
				}
				finish(); 
			}
		});

		refreshDate();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( data != null && requestCode == DIALOG_DATE_PICKER_ID ) {
			Date curDate = new Date();
			long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
			Date newDate = new Date(ct);
			date = newDate;
			refreshDate();
		}
	}
	
	private void refreshDate() {
		SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());		
		((TextView)findViewById(R.id.tvDate)).setText(sd.format(date));		
	}
}
