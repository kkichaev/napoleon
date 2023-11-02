package com.grsoft.util.view.dialog_helper;
import com.grsoft.aceteam.R;

import java.util.Calendar;
import java.util.Date;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.widget.TextView;
import android.widget.TimePicker;

public class TimeHandler extends DateHandler {
	
	public TimeHandler(TextView tv, Date date, int dialogId) { super(tv, date, dialogId); }

	public Dialog createDialog() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		Dialog ret = 
			new TimePickerDialog(tv.getContext(),
				new TimePickerDialog.OnTimeSetListener() {

					@Override
					public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
						Calendar c = Calendar.getInstance();
						Calendar c1 = Calendar.getInstance();
						
						c1.setTime(date);
						c.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DAY_OF_MONTH), hourOfDay, minute, 0);
						
						date = c.getTime();
						updateDate();
					}
				},
				calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
		return ret;
	}
	
	public Date adjustTime(Date d) {
		Calendar c = Calendar.getInstance();
		Calendar c1 = Calendar.getInstance();
		
		c1.setTime(d);
		c.setTime(date);
		c.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DAY_OF_MONTH), 
				c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), 0);
		
		return c.getTime();
	}

	@Override protected String displayFormat() { return "HH:mm"; }
}
