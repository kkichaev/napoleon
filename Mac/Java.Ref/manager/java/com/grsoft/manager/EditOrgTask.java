package com.grsoft.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.ManagerOrgTask;
import com.grsoft.napoleon.util.CalendarDlg;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ManagerOrgTaskExport;
import com.grsoft.network.ObjectListener;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class EditOrgTask extends Activity implements UpdateCtrl {
	
	protected static final int SELECT_FROM_DATE = 0;
	protected static final int SELECT_TILL_DATE = 1;
	private static final int ERROR_WRITE = 2;
	ManagerOrgTask task;
	
	public static void open(Context context, ManagerOrgTask task, String userid, String orgId) {
		Intent i = new Intent(context, EditOrgTask.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, task == null ? "" : task.id);
		i.putExtra(ExtrasConst.USER_ID_STR, userid);
		i.putExtra(ExtrasConst.ORG_ID_STR, orgId);
		
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.edit_task);
		
		task = new ManagerOrgTask();
		task.start = new Date();
		task.finish = new Date();
		Bundle b = getIntent().getExtras();
		String id = b.getString(ExtrasConst.DOC_ROW_ID_STR);
		task.orgid = b.getString(ExtrasConst.ORG_ID_STR);
		task.userid = b.getString(ExtrasConst.USER_ID_STR);
		
		if( id != null && id.length() > 0 ) {
			DbReader r = new DbReader();
			r.select(task, task.getTableName(), "id='" + id + "'");
			r.close();
		}
		
		EditText ed = (EditText)findViewById(R.id.edTask);
		ed.setText(task.text);
		
		refreshDate();
		
		findViewById(R.id.tvFrom).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { showDialog(SELECT_FROM_DATE); }
		});
		findViewById(R.id.tvTill).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { showDialog(SELECT_TILL_DATE); }
		});
		
		findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { finish(); }
		});
		
		findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				saveAndSend();
				finish();
			}
		});
	}

	void send() {
		List<ObjectListener> toSend = new ArrayList<ObjectListener>();
		toSend.add(new ManagerOrgTaskExport(task));
		UpdateProcess up = new UpdateProcess(this, this, new ArrayList<Hitching>());
		up.setSending(toSend);
		
		up.execute((Void[])null);		
	}
	
	protected void saveAndSend() {
		Config config = ConfigManager.getConfig();

		task.params = ManagerOrgTask.DIRTY;
		task.created = Util.getDateTime();
		task.manager = config.login;
		task.text = ((EditText)findViewById(R.id.edTask)).getText().toString();
		if( task.id.length() == 0 )
			task.id = UUID.randomUUID().toString().replace("-", "");
		DbWriter wr = new DbWriter();
		wr.insertRecord(task);
		wr.close();

		send();
	}

	@SuppressLint("SimpleDateFormat")
	private void refreshDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		
		String str = sdf.format(task.start);
		SpannableString content = new SpannableString(str);
		content.setSpan(new UnderlineSpan(), 0, str.length(), 0);

		((TextView)findViewById(R.id.tvFrom)).setText(content);
		
		str = sdf.format(task.finish);
		content = new SpannableString(str);
		content.setSpan(new UnderlineSpan(), 0, str.length(), 0);
		((TextView)findViewById(R.id.tvTill)).setText(content);
	}

	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		
		if( id == SELECT_FROM_DATE || id == SELECT_TILL_DATE ) {
			CalendarDlg.setCurrentDate(dialog, (id == SELECT_FROM_DATE) ? task.start : task.finish);
		}
	}

	@Override
	protected Dialog onCreateDialog(final int id) {
		if( id == SELECT_FROM_DATE || id == SELECT_TILL_DATE ) {
			return CalendarDlg.create(this, new CalendarDlg.Handler() {
				
				@Override
				public void selectedDate(Date d) {
					d = Util.resetTime(d);
					if(id == SELECT_FROM_DATE)
						task.start = d;
					else
						task.finish = d;
					
					refreshDate();
				}
			});
		} else if(id == ERROR_WRITE) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle(R.string.error);
			b.setMessage(R.string.write_error);
			b.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					send();
				}
			});
			
			b.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					finish();
				}
			});
			return b.create();
		}
		
		return super.onCreateDialog(id);
	}

	@Override public void updateCtrl(boolean enabled) { }

	@Override
	public void onFinish(boolean success) {
		if( success )
			finish();
		else 
			showDialog(ERROR_WRITE);
	}
}
