package com.grsoft.ads;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.grsoft.ads.dataobjects.TaskResponce;
import com.grsoft.ads.dataobjects.impl.TaskResponceImpl;
import com.grsoft.napoleon.dataobjects.TaskQuery;
import com.grsoft.napoleon.dataobjects.impl.TaskImpl;
import com.grsoft.util.ExtrasConst;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;


public class ApplyTask extends BaseDialogFragment {
	public static final Class<? extends DialogFragment> dialog = ApplyTask.class; 
	protected TaskImpl task = new TaskImpl();
	private DatePicker datePicker;
	private View btnOK;
	private View btnCancel;
	private Button btnStart;
	private Button btnFinish;
	
	public static void open(Activity activity, String taskid) {
		try{
			DialogFragment dlg = dialog.newInstance();
			Bundle args = new Bundle();
			args.putString(TASKID, taskid);
			dlg.setArguments(args);
			dlg.show(activity.getFragmentManager(), dialog.getCanonicalName());
		}catch(Exception e){ 
			e.printStackTrace(); 
		}
	}
	
	@Override
	public int getLayoutID() { return R.layout.applytask; }

	@Override
	protected void inflateView(View view) {
		datePicker = (DatePicker) view.findViewById(R.id.datePicker);
		btnStart = (Button) view.findViewById(R.id.btnStart);
		btnFinish = (Button) view.findViewById(R.id.btnFinish);
		btnCancel = view.findViewById(R.id.btnCancel);
		btnOK = view.findViewById(R.id.btnOK);
	}

	@Override
	protected void init() {
		TaskQuery t = task.getData();
		t.taskid = getArguments().getString(TASKID);
		task.read();
		task.close();
	}

	@Override
	protected void initView() {
		if(task.getRowid() != ExtrasConst.INVALID_ROWID){
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			
			TaskQuery t = task.getData();
			
			Calendar c = Calendar.getInstance();
			c.setTime(t.start);
			datePicker.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
			
			btnStart.setText(sdf.format(t.start));
			btnStart.setTag(R.id.hour, c.get(Calendar.HOUR_OF_DAY));
			btnStart.setTag(R.id.minute, c.get(Calendar.MINUTE));
			btnStart.setOnClickListener(setStartDlg());
			
			c.setTime(t.finish);
			btnFinish.setText(sdf.format(t.finish));
			
			btnFinish.setOnClickListener(setFinishDlg());
			btnFinish.setTag(R.id.hour, c.get(Calendar.HOUR_OF_DAY));
			btnFinish.setTag(R.id.minute, c.get(Calendar.MINUTE));
			btnOK.setOnClickListener(applyClick());
			btnCancel.setOnClickListener(cancelClick());
		}
	}
	
	class TimeDlg extends DialogFragment{
		public static final String BEGIN = "begin";
		public static final String CONTROL = "control";
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.select_time);
			TimePicker timepicker = new TimePicker(getActivity());
			timepicker.setId(R.id.timepicker);
			timepicker.setIs24HourView(true);
			
			boolean begin = getArguments().getBoolean(BEGIN); 
			View v = btnStart;
			
			if (!begin)
				v = btnFinish;
				
			timepicker.setCurrentHour((Integer) v.getTag(R.id.hour));
			timepicker.setCurrentMinute((Integer) v.getTag(R.id.minute));
				
			builder.setView(timepicker);
			builder.setPositiveButton(R.string.ok, setTime(begin));
			builder.setNegativeButton(R.string.cancel, null);
			
			return builder.create();
		}
	}

	private OnClickListener setStartDlg() {	return new OnClickListener() { @Override public void onClick(View v) { showTimeDlg(true);}}; }
	private OnClickListener setFinishDlg() { return new OnClickListener() {	@Override public void onClick(View v) {	showTimeDlg(false);	}}; }

	public android.content.DialogInterface.OnClickListener setTime(final boolean begin) {
		return new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				TimePicker tp = (TimePicker) ((Dialog)dialog).findViewById(R.id.timepicker);
				View v = btnStart;
				
				if(!begin)
					v = btnFinish;
				
				int h = tp.getCurrentHour();
				int m = tp.getCurrentMinute();
				
				((Button)v).setText(String.format("%02d:%02d", h, m));
				v.setTag(R.id.hour, h);
				v.setTag(R.id.minute, m);
			}
		};
	}

	private OnClickListener cancelClick() {	return new OnClickListener() { @Override public void onClick(View v) { dismiss(); } }; }

	private OnClickListener applyClick() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				Calendar c = Calendar.getInstance();
				c.set(Calendar.YEAR, datePicker.getYear());
				c.set(Calendar.MONTH, datePicker.getMonth());
				c.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
				c.set(Calendar.HOUR_OF_DAY, (Integer) btnStart.getTag(R.id.hour));
				c.set(Calendar.MINUTE, (Integer) btnStart.getTag(R.id.minute));
				c.set(Calendar.SECOND, 0);
				c.set(Calendar.MILLISECOND, 0);
				
				Date start = c.getTime();
				c.set(Calendar.HOUR_OF_DAY, (Integer) btnFinish.getTag(R.id.hour));
				c.set(Calendar.MINUTE, (Integer) btnFinish.getTag(R.id.minute));
				Date finish = c.getTime();
				
				if (finish.getTime() > start.getTime()){
					TaskResponceImpl impl = new TaskResponceImpl();
					impl.init(v.getContext(), task.getData());
					TaskResponce tr = impl.getData();
					tr.solution = TaskQuery.APPLY;
					impl.write();
					impl.close();
					
					task.getData().solution = TaskQuery.APPLY;
					task.write();
					task.close();
					
					dismiss();
					
					getActivity().sendBroadcast(new Intent(AdsService.SYNC_ACTION));
					getActivity().finish();
				}else
					Toast.makeText(getActivity(), R.string.time_error, Toast.LENGTH_SHORT).show();
			}
		};
	}

	protected void showTimeDlg(boolean begin) {
		Bundle arg = new Bundle();
		arg.putBoolean(TimeDlg.BEGIN, begin);
		TimeDlg dlg = new TimeDlg();
		dlg.setArguments(arg); 
		dlg.show(getFragmentManager(), dlg.getClass().getCanonicalName());
	}
}
