package com.grsoft.napoleon;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import com.grsoft.dataobjects.impl.RestockImpl;
import com.grsoft.util.ExtrasConst;
import com.grsoft.view.BaseActivity;

public class RestockCreateDetail extends BaseActivity {
	RestockImpl doc = new RestockImpl();
	
	static public void open(Context context, RestockImpl doc) {
		
		Intent i = new Intent(context, RestockCreateDetail.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.restock_create_detail);
		
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		long rid = b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		doc.read(rid);
		
		findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) {
				doc.delete();
				finish();
			}
		});
		
		findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Date start = getDate((DatePicker)findViewById(R.id.dtpStart));
				Date end = new Date(getDate((DatePicker)findViewById(R.id.dtpEnd)).getTime() + 24 * 3600 * 1000l);
				
				doc.loadItems(start, end);
				doc.write();
				doc.open(RestockCreateDetail.this);
				finish();
			}
		});
	}
	
	Date getDate(DatePicker dp) {
	    int day = dp.getDayOfMonth();
	    int month = dp.getMonth();
	    int year =  dp.getYear();

	    Calendar calendar = Calendar.getInstance();
	    calendar.set(year, month, day);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

	    return calendar.getTime();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		doc.close();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
	}
}
