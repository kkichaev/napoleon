package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.List;
import com.grsoft.dataobjects.OrgTask;
import com.grsoft.dataobjects.VisitEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.OrgTaskExecImpl;
import com.grsoft.dataobjects.impl.OrgTaskImpl;
import com.grsoft.napoleon.util.OrgTaskListHelper;
import com.grsoft.util.gps.GPSUtilNew;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;

public class VisitEditEx extends VisitEdit {
	OrgImpl oi = new OrgImpl();
	private List<Long> curTaskList;
	private static final int NOTIFY_TASK_DLG = R.id.notify_task_dlg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		curTaskList = new OrgTaskListHelper().getTaskList(visit.getId(), true);
	}

	@Override
	protected int getContentView() {
		return R.layout.visiteditex;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case NOTIFY_TASK_DLG:
			return createNotifyTaskDlg();
		default:
			return super.onCreateDialog(id);
		}
	}

	private Dialog createNotifyTaskDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.current_tasks);
		builder.setMessage("");
		return builder.create();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case NOTIFY_TASK_DLG:
			prepareNotifyTaskDlg(dialog);
		default:
			super.onPrepareDialog(id, dialog);
		}
	}

	private SimpleDateFormat sdf = new SimpleDateFormat("dd:MM");

	private void prepareNotifyTaskDlg(Dialog dialog) {
		StringBuilder sb = new StringBuilder();
		OrgTaskImpl taskImpl = new OrgTaskImpl();

		int cnt = 1;
		for (Long rowid : curTaskList) {
			if (taskImpl.read(rowid)) {
				OrgTask task = taskImpl.getData();
				StringBuilder range = new StringBuilder();
				range.append(sdf.format(task.start)).append(" - ")
						.append(sdf.format(task.finish));
				sb.append(cnt++).append(") ").append(range.toString())
						.append("<br>");
				sb.append(task.text);
			}
		}

		taskImpl.close();

		((AlertDialog) dialog).setMessage(Html.fromHtml(sb.toString()));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK){
			
			if (Features.DEL_VISIT_WITHOUT_PHOTO && 
					(visit.getData().items == null || visit.getData().items.size() == 0))
				showDialog(ASK_TO_DEL_VISIT_MSG);
			else{
				if (!saveVisit())
					visit.delete();
				
				finish();
			}
			
			final VisitEx o = (VisitEx) visit.getData();
			
			if(curTaskList != null && curTaskList.size() > 0){
				OrgTaskExecImpl taskExec = new OrgTaskExecImpl(); 
				if(taskExec.init(this, o.id, GPSUtilNew.getLastKnownLocation()))
					OrgTaskList.open(this, o.id, taskExec.getRowid());
			}
			
			return true;
		}else
			return super.onKeyDown(keyCode, event);
	}
}
