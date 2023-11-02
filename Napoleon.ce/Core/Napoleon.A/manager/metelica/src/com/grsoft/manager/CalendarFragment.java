package com.grsoft.manager;

import java.util.Date;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import com.grsoft.util.view.CalendarView;


public class CalendarFragment extends DialogFragment {
	public static final String RANGE = "range";
	public static final String START = "start";
	public static final String FINISH = "finish";
	public static final String DATE = "date";
	
	public interface CalendarOnSelect{
		public void select(String range, Date date);
	};
	
	private CalendarOnSelect select;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.select_date);
		final CalendarView calendar = new CalendarView(getActivity());
		Date date = new Date(getArguments().getLong(DATE));
		calendar.setCurrentDate(date);
		calendar.setMarkDate(date);
		builder.setView(calendar);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(select != null)
							select.select(getArguments().getString(RANGE), calendar.getCurrentDate()); 
					}
				});
		builder.setNegativeButton(R.string.cancel, null);
		return builder.create();
	}
	
	public void setCalendarOnSelect(CalendarOnSelect select){
		this.select = select;
	}
}
