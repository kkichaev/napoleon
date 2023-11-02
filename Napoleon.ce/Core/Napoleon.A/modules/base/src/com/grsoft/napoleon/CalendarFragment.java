package com.grsoft.napoleon;

import java.util.Date;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.view.CalendarView;

public class CalendarFragment extends DialogFragment implements CalendarView.OnCalendarActionListener {
	public static final String MARK_DATE = "MarkDate";
	public static final String DATE_CHANGE_ACTION = "com.grsoft.napoleon.CalendarFragment.DATE_CHANGE_ACTION";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.calendar, null, false);
		
		getDialog().setTitle(R.string.select_date);
		
		Bundle args =  getArguments();
		Date curDate = new Date();
		Date mark = null;
		
		if(args != null){
			curDate = new Date(args.getLong(ExtrasConst.DATE_TAG, curDate.getTime()));
			
			long mt = args.getLong(MARK_DATE, ExtrasConst.INVALID_ROWID);
			
			if(mt != ExtrasConst.INVALID_ROWID)
				mark = new Date(mt);
		}
		
		CalendarView cv = (CalendarView)view.findViewById(R.id.calendar);
		cv.setCurrentDate(curDate);
		cv.setCalendarActionListener(this);
		
		if(mark != null ) 
			cv.setMarkDate(mark);
		
		return view;
	}

	@Override
	public void onDateChanged(Date oldDate, Date newDate) {
		Intent i = new Intent(DATE_CHANGE_ACTION);
		i.putExtra(ExtrasConst.DATE_TAG, newDate.getTime());
		getActivity().sendBroadcast(i);
		dismiss();
	}

	@Override
	public void onOtherDateChanged(Date currentDate, Date otherDate) {
	}

	@Override
	public void onCalendarCancelled() {
	}
}
