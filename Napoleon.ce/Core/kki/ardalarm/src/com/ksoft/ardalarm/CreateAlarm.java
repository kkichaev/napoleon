package com.ksoft.ardalarm;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import com.ksoft.ardalarm.database.DataBase;
import com.ksoft.ardalarm.database.TimeAlarm;

public class CreateAlarm extends Activity {
	public static final String ACTION = "com.ksoft.ardalarm.CreateAlarm";
	private Button btnSave;
	private Button btnCancel;
	private EditText edName;
	private TimePicker timePicker;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.createalarm);
		
		btnSave = (Button) findViewById(R.id.btnSave);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		edName = (EditText) findViewById(R.id.edName);
		timePicker = (TimePicker) findViewById(R.id.timePicker);
		
		btnSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				save();
				finish();
			}
		});
		
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	protected void save() {
		ContentValues cv = new ContentValues();
		cv.put(TimeAlarm.NAME, edName.getText().toString());
		cv.put(TimeAlarm.MINUTE, timePicker.getCurrentMinute());
		cv.put(TimeAlarm.HOUR, timePicker.getCurrentHour());
		cv.put(TimeAlarm.PERIOD, getPeriod());
		SQLiteDatabase db = new DataBase(this).getWritableDatabase();
		db.insert(TimeAlarm.TABLE_NAME, null, cv);
	}

	private int getPeriod() {
		return 0;
	}
}
