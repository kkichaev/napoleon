package com.grsoft.napoleon;
import com.grsoft.aceteam.R;

import java.util.Date;

import com.grsoft.aceteam.R;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.view.CalendarView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class CalendarActivity extends Activity implements CalendarView.OnCalendarActionListener {
		
	public static final String MARK_DATE = "MarkDate";
	
	CalendarView.DateMarker dateMarker;

	static CalendarView.CalendarHandler handler;

	public static Intent open(Context context, Date date, CalendarView.CalendarHandler handler) {
		CalendarActivity.handler = handler;
		Intent i = new Intent(context, CalendarActivity.class);
		i.putExtra(ExtrasConst.DATE_TAG, date.getTime());
		return i;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar);
		
		Bundle b = (savedInstanceState != null) ? savedInstanceState : getIntent().getExtras();
		Date curDate = new Date();
		long ct = b == null ? curDate.getTime() : b.getLong(ExtrasConst.DATE_TAG, curDate.getTime());
		Date newDate = new Date(ct);
		
		
		CalendarView cv = (CalendarView)findViewById(R.id.calendar);
		cv.setCurrentDate(newDate);
		cv.setCalendarActionListener(this);
		ct = b == null? -1 : b.getLong(MARK_DATE, -1);
		if(ct != -1 ) {
			cv.setMarkDate(new Date(ct));
		}
		setDateHandler(handler);
	}
	
	public void setDateMarker(CalendarView.DateMarker marker) {
		CalendarView cv = (CalendarView)findViewById(R.id.calendar);
		cv.setMarker(marker);
	}

	@Override
	public void onDateChanged(Date oldDate, Date newDate) {
		Intent i = new Intent();
		i.putExtra(ExtrasConst.DATE_TAG, newDate.getTime());
		setResult(RESULT_OK, i);
		finish();
	}
	public void setDateHandler(CalendarView.CalendarHandler handler) {
		CalendarView cv = (CalendarView)findViewById(R.id.calendar);
		cv.setHandler(handler);
	}

	@Override
	public void onOtherDateChanged(Date currentDate, Date otherDate) {
	}

	@Override
	public void onCalendarCancelled() {
	}
}
