package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgTask;
import com.grsoft.dataobjects.impl.OrgTaskImpl;
import com.grsoft.napoleon.util.OrgTaskListHelper;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.DocFilterOnClickListenerEx;
import com.grsoft.util.Util;

public class DocumentsEx extends Documents {
	private List<Long> curTaskList;
	private static final int NOTIFY_TASK_DLG = R.id.notify_task_dlg;
	private SimpleDateFormat sdf = new SimpleDateFormat("dd:MM");
	
	protected DocFilterOnClickListener createDocFilter() {
		return new DocFilterOnClickListenerEx(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		OrgEx orgEx = (OrgEx) org.getData();
		TextView tvLicense = (TextView) findViewById(R.id.tvLicense);
		
		tvLicense.setText(getString(R.string.cheif_expired, 
				orgEx.chexp.getYear() <= 70 ? "..." : 
				Util.simpleDateFormat.format(orgEx.chexp)));
		
		Calendar c = Calendar.getInstance();
		c.setTime(Util.getDate());
		c.add(Calendar.DAY_OF_MONTH, -1);
		
		tvLicense.setTextColor(orgEx.chexp.getYear() > 70 &&
				orgEx.chexp.getTime() <= c.getTime().getTime() ?
						getResources().getColor(R.color.red) : getResources().getColor(R.color.black));
		
		if(Features.ORG_TASK){
			curTaskList = new OrgTaskListHelper()
				.getTaskList(org.getData().id, true);
			
			if(curTaskList.size() > 0)
				showDialog(NOTIFY_TASK_DLG);
		}
		
	}
	
	@Override
	protected int getContentViewID() {
		return R.layout.documentsex;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == NOTIFY_TASK_DLG)
			return createNotifyTaskDlg();
		else return super.onCreateDialog(id);
	}
	
	private Dialog createNotifyTaskDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.current_tasks);
		builder.setMessage("");
		return builder.create();
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if (id == NOTIFY_TASK_DLG) 
			prepareNotifyTaskDlg(dialog);
		else
			super.onPrepareDialog(id, dialog);
	}
	
	private void prepareNotifyTaskDlg(Dialog dialog) {
		StringBuilder sb = new StringBuilder();
		OrgTaskImpl taskImpl = new OrgTaskImpl();
		
		int cnt = 1;
		for(Long rowid: curTaskList){
			if(taskImpl.read(rowid)){
				OrgTask task = taskImpl.getData();
				StringBuilder range = new StringBuilder();
				range.append(sdf.format(task.start)).append(" - ")
						.append(sdf.format(task.finish));
				sb.append(cnt++).append(") ").append(range.toString()).append("<br>");
				sb.append(task.text);
			}
		}
		
		taskImpl.close();
		
		((AlertDialog)dialog).setMessage(Html.fromHtml(sb.toString()));
	}
}
