package com.grsoft.napoleon.dostavka;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.grsoft.database.DataBaseManager;
import com.grsoft.napoleon.CalendarActivity;
import com.grsoft.util.view.CalendarView;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

public class RouteCalendar extends CalendarActivity implements CalendarView.DateMarker {
	
	public static int CALENDAR_REQ = 0x1245;
	
	List<Long> activeDates = new ArrayList<Long>();
	int tzOffset;	
	
	public static void open(Activity context) {
		Intent i = new Intent(context, RouteCalendar.class);
		context.startActivityForResult(i, CALENDAR_REQ);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
						
		TimeZone tz = TimeZone.getDefault();
		tzOffset = tz.getOffset((new Date()).getTime()) + 3600 * 1000; // move to first hour of local time
		
		String sql = "select distinct((start + " + Integer.toString(tzOffset) + ")/(1000 * 24 * 3600)) from route where hidden = 0";
		
		try {
			Cursor c = DataBaseManager.getDataBase().rawQuery(sql, null);
			while(c.moveToNext()) {
				long dt = c.getLong(0);// + offset;
				activeDates.add(dt);
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		setDateMarker(this);
	}

	@Override
	public boolean isMarked(long date) {
		date = (date + tzOffset) /(1000 * 24 * 3600);
		return activeDates.contains(date);
	}
}
