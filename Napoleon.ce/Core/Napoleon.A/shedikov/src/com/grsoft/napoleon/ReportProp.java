package com.grsoft.napoleon;

import java.util.Calendar;
import java.util.Date;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import com.grsoft.dataobjects.ReportsRequest;
import com.grsoft.dataobjects.impl.ReportsRequestImpl;
import com.grsoft.util.Util;


public class ReportProp extends DialogFragment {
	private static final String REP_ID = "rep_id";
	
	public static void show(FragmentActivity owner, String id){
		DialogFragment dlg = new ReportProp();
		Bundle arg = new Bundle();
		arg.putString(REP_ID, id);
		dlg.setArguments(arg);
		dlg.show(owner.getSupportFragmentManager(), dlg.getClass().getCanonicalName());
	}
	
	private DatePicker dpStart;
	private DatePicker dpFinish;
	private String id = "";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.reportprop, null, false);

		dpStart = (DatePicker)view.findViewById(R.id.dpStart);
		dpFinish = (DatePicker) view.findViewById(R.id.dpFinish);
		
		id = getArguments().getString(REP_ID);
				
		view.findViewById(R.id.btnCancel).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		
		view.findViewById(R.id.btnOK).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				apply();
			}
		});
		
		getDialog().setTitle(R.string.requestreport);
		
		return view;
	}

	protected void apply() {
		ReportsRequestImpl rri = new ReportsRequestImpl();
		ReportsRequest rr = rri.getData();
		rr.id = id;
		rr.start = getDate(dpStart);
		rr.finish = getDate(dpFinish);
		rr.created = Util.getDateTime();
		rri.write();
		rri.close();
		getActivity().sendBroadcast(new Intent(ReportListEx.REFRESH_ACTION));
		dismiss();
	}
	
	public static Date getDate(DatePicker datePicker){
	    int day = datePicker.getDayOfMonth();
	    int month = datePicker.getMonth();
	    int year =  datePicker.getYear();

	    Calendar calendar = Calendar.getInstance();
	    calendar.set(year, month, day);

	    return calendar.getTime();
	}
}
