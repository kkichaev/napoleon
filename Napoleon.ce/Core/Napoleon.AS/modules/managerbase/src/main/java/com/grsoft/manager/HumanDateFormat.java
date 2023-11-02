package com.grsoft.manager;

import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.grsoft.util.DatePeriod;

import android.content.Context;

public class HumanDateFormat extends SimpleDateFormat {
	private static final long serialVersionUID = 1L;
	private final String TODAY;
	private final String YESTERDAY;
	private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	private final static String FORMAT = "dd.MM.yyyy HH:mm";
	
	public HumanDateFormat(Context context) {
		super(FORMAT);
		TODAY = context.getString(R.string.today);
		YESTERDAY = context.getString(R.string.yesterday);
	}
	
	@Override
	public StringBuffer format(Date date, StringBuffer buffer, FieldPosition fieldPos) {
		Date now = Calendar.getInstance().getTime();
		long d = DatePeriod.daysDiff(date, now);
		
		if(d == 0)
			buffer.append(String.format("%s %s", TODAY, sdf.format(date)));
		else if (d == 1)
			buffer.append(String.format("%s %s", YESTERDAY, sdf.format(date)));
		else
			super.format(date, buffer, fieldPos);
		
		return buffer;
	}
}
