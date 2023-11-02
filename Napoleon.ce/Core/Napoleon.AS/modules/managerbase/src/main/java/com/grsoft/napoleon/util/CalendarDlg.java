package com.grsoft.napoleon.util;

import java.util.Date;

import com.grsoft.manager.R;
import com.grsoft.util.view.CalendarView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

public class CalendarDlg {
	
	public interface Handler {
		public void selectedDate(Date d);
	}
	
	public static void setCurrentDate(Dialog dialog, Date curDate) {
		((CalendarView)((AlertDialog)dialog).findViewById(R.id.calendar)).setCurrentDate(curDate);
	}
	
	public static Dialog create(Context context, final Handler handler) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.select_date);
		
		builder.setView(View.inflate(context, R.layout.calendar_dlg, null));
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Date d = ((CalendarView)((AlertDialog)dialog).findViewById(R.id.calendar)).getCurrentDate();
						handler.selectedDate(d);
					}
				});
		
		builder.setNegativeButton(R.string.cancel, null);
		Dialog ret = builder.create();
		return ret;
	}
}
