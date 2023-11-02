package com.grsoft.ads;

import java.text.SimpleDateFormat;

import com.grsoft.ads.dataobjects.Note;
import com.grsoft.ads.dataobjects.TaskAttachmentInfo;
import com.grsoft.ads.dataobjects.TaskResponce;
import com.grsoft.ads.dataobjects.impl.NoteImpl;
import com.grsoft.ads.dataobjects.impl.TaskResponceImpl;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.napoleon.dataobjects.TaskQuery;
import com.grsoft.napoleon.dataobjects.impl.TaskImpl;
import com.grsoft.network.BaseFragmentActivity;
import com.grsoft.util.gps.GPSUtilNew;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class TaskPreview extends BaseFragmentActivity{
	public static final Class<? extends BaseFragmentActivity> activity = TaskPreview.class; 
	
	protected View btnOK;
	protected View btnCancel;
	protected TextView tvDate;
	protected TextView tvTask;
	protected TextView tvOrg;
	protected TextView tvAddress;
	protected TextView tvFio;
	protected TextView tvPhone;
	protected  View btnNote;
	private ImageButton ivAttachment;
	
	protected TaskImpl task = new TaskImpl();
	
	public static void open(Context context, String taskid) {
		Intent intent = new Intent(context, activity);
		intent.putExtra(AdsConsts.TASKID, taskid);
		context.startActivity(intent);
	}

	protected void init() {
		String taskid = getIntent().getStringExtra(AdsConsts.TASKID);
		task.read("taskid", taskid);
	}

	protected void initView() {
		((Button)btnOK).setText(R.string.apply);
		btnOK.setOnClickListener(applyClick());
		((Button)btnCancel).setText(R.string.reject);
		btnCancel.setOnClickListener(rejectClick());
		
		TaskQuery t = task.getData();
		SimpleDateFormat sdf = new SimpleDateFormat("EE. dd.MM.yyyy");
		
		tvTask.setText(t.text);
		tvTask.setMovementMethod(new ScrollingMovementMethod());
		tvAddress.setText(t.address);
		tvOrg.setText(t.client);
		tvAddress.setOnClickListener(addressClick());
		
		StringBuilder sb = new StringBuilder();
		sb.append(sdf.format(t.start)).append(" ");
		
		sdf = new SimpleDateFormat("HH:mm");
		sb.append(sdf.format(t.start)).append(" - ").append(sdf.format(t.finish));
		
		tvDate.setText(sb.toString());
		
		tvFio.setText(t.fio);
		tvPhone.setText(t.phone);
		tvPhone.setOnClickListener(phoneClick());
		
		btnNote.setOnClickListener(noteClick());
	}
	
	private OnClickListener phoneClick(){
		return new OnClickListener(){
			@Override
			public void onClick(View v) {
				String phone = ((TextView) v).getText().toString();
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(String.format("tel: %s", phone)));
				startActivity(intent);
			}
		};
	}
	
	private OnClickListener addressClick() {
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String address = ((TextView)v).getText().toString();
				String uri = String.format("geo:0,0?q=%s", address );
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
				startActivity(intent); 
			}
		};
	}

	protected OnClickListener noteClick() {
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				NoteDlg dlg = new NoteDlg();
				dlg.show(getFragmentManager(), dlg.getClass().getCanonicalName());
			}
		};
	}
	
	public static class NoteDlg extends DialogFragment{
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.order_title);
			TaskPreview taskPreview = (TaskPreview) getActivity();
			EditText ed = new EditText(taskPreview);
			ed.setId(R.id.text);
			builder.setView(ed);
			builder.setPositiveButton(R.string.ok, taskPreview.noteokclick());
			builder.setNegativeButton(R.string.cancel, null);
			
			return builder.create();
		}
	}
	
	public static class AskToRejectDlg extends DialogFragment{
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.rejecttask, null, false);
			
			TextView tv = (TextView) view.findViewById(R.id.tvMessage);
			tv.setText(R.string.asktoreject);
			
			View btnOK = view.findViewById(R.id.btnOK);
			btnOK.setOnClickListener(okClick());
			View btnCancel = view.findViewById(R.id.btnCancel);
			btnCancel.setOnClickListener(cancelClick());
			return view;
		}
		
		private OnClickListener okClick() {
			return new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					EditText ed = (EditText) getDialog().findViewById(R.id.edText);
					String txt = ed.getText().toString().trim();
					
					if(txt.length() > 0){
						((TaskPreview)getActivity()).rejectTask(txt);
						dismiss();
					}else
						Toast.makeText(getActivity(), R.string.input_reject_cause, Toast.LENGTH_SHORT).show();
				}
			};
		}

		private OnClickListener cancelClick() {	return new OnClickListener() { @Override public void onClick(View v) { dismiss();} };}
	}
	
	private void rejectTask(String cause) {
		TaskResponceImpl impl = new TaskResponceImpl();
		impl.init(getContext(), task.getData());
		TaskResponce r = impl.getData();
		r.solution = TaskQuery.REJECT;
		r.remark = cause;
		impl.write();
		impl.close();
		
		
		task.getData().solution = TaskQuery.REJECT;
		task.write();
		task.close();
		
		sendBroadcast(new Intent(AdsService.SYNC_ACTION));
		finish();
	}

	public DialogInterface.OnClickListener noteokclick() {
		return new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditText ed = ((Dialog)dialog).findViewById(R.id.text);
				String text = ed.getText().toString().trim();
				
				if(text.length() > 0){
					TaskQuery t =  task.getData();
					NoteImpl note = new NoteImpl();
					note.init(TaskPreview.this, t.id, GPSUtilNew.getLastKnownLocation());
					Note n = note.getData();
					n.taskid = t.taskid;
					n.client = t.client;
					n.address = t.address;
					n.remark = text;
					note.write();
					note.close();
					sendBroadcast(new Intent(AdsService.SYNC_ACTION));
				}
			}
		};
	}

	private OnClickListener rejectClick() {
		return new OnClickListener() { @Override public void onClick(View v) { 
				AskToRejectDlg atr = new AskToRejectDlg();
				atr.show(getFragmentManager(), AskToRejectDlg.class.getCanonicalName());
			}
		};
	}

	private OnClickListener applyClick() {
		return new OnClickListener() { @Override public void onClick(View v) { 
			//ApplyTask.open(TaskPreview.this, task.getData().taskid);
			
			TaskResponceImpl impl = new TaskResponceImpl();
			impl.init(v.getContext(), task.getData());
			TaskResponce tr = impl.getData();
			tr.solution = TaskQuery.APPLY;
			
			impl.write();
			impl.close();
			
			task.getData().solution = TaskQuery.APPLY;
			task.write();
			task.close();
			
			sendBroadcast(new Intent(AdsService.SYNC_ACTION));
			finish();
		}};
	}

	protected void inflateView() {
		btnOK = findViewById(R.id.btnOK);
		btnCancel = findViewById(R.id.btnCancel);
		tvDate = (TextView) findViewById(R.id.tvDate);
		tvTask = (TextView) findViewById(R.id.tvTask);
		tvOrg = (TextView) findViewById(R.id.tvOrg);
		tvAddress = (TextView) findViewById(R.id.tvAddress);
		tvFio = (TextView) findViewById(R.id.tvFio);
		tvPhone = (TextView) findViewById(R.id.tvPhone);
		btnNote = findViewById(R.id.btnNote);
	}

	public int getLayoutID() { return R.layout.taskprop; }
	
	protected boolean taskHasAttachments() {
		class AttachmenChecker
		{
			public boolean hasAttach = false;
		}
		
		final AttachmenChecker result = new AttachmenChecker();
		
		DataTraveler.travel(TaskAttachmentInfo.class, new DataTraveler.Travel<TaskAttachmentInfo>() {

			@Override
			public boolean travel(DataTraveler<TaskAttachmentInfo> item) {
				result.hasAttach = true;
				return false;
			}}, 
				String.format("taskid='%s'", task.getData().taskid));
		return result.hasAttach;
	}
}
