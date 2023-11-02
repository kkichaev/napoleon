package com.grsoft.util.view.dialog_helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

public class DateHandler {
	
	public interface Handler {
		boolean canSetDate(Date newDate);
	}
	
	protected Date date;
	TextView tv;
	int id;
	Handler handler = null;
	
	public DateHandler(TextView dateView, Date date, int dialogId) {
		this.tv = dateView;
		this.date = date;
		this.id = dialogId;
		
		tv.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { ((Activity)tv.getContext()).showDialog(id); }
		});
		
		updateDate();
	}
	
	public void setHandler(Handler h) { handler = h; }
	
	public Dialog createDialog() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		Dialog ret = 
			new DatePickerDialog(tv.getContext(),
				new DatePickerDialog.OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
						Calendar c = Calendar.getInstance();
						Calendar c1 = Calendar.getInstance();

						c1.setTime(date);
						c.set(year, monthOfYear, dayOfMonth, c1.get(Calendar.HOUR_OF_DAY), c1.get(Calendar.MINUTE), 0);
						c.set(Calendar.MILLISECOND, 0);
						if( handler == null || handler.canSetDate(c.getTime()) ) {
							date = c.getTime();
							updateDate();
						}
					}
				},
				calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		return ret;
	}
	
	public Date getDate() { return date; }

	protected String displayFormat() { return "dd.MM.yyyy"; }
	
	public void updateDate() {
		SimpleDateFormat sd = new SimpleDateFormat(displayFormat(), Locale.getDefault());		
		tv.setText(sd.format(date));		
	}
}
