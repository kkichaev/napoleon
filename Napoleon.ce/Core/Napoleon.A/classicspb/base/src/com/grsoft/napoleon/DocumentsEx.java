package com.grsoft.napoleon;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import com.grsoft.dataobjects.impl.OrgTaskImpl;
import com.grsoft.napoleon.util.OrgTaskListHelper;

public class DocumentsEx extends Documents {
	
	private static final int SHOW_TASK_DLG = 1;
	private ArrayList<Long> data = new ArrayList<Long>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		data.addAll(new OrgTaskListHelper().getTaskList(org.getData().id, true));
		
		if(data.size() > 0)
			showDialog(SHOW_TASK_DLG);
		
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case SHOW_TASK_DLG: return createShowTaskDlg();
		default: return super.onCreateDialog(id);
		}
	}

	private Dialog createShowTaskDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		CharSequence[] t = new CharSequence[data.size()];
		
		OrgTaskImpl impl = new OrgTaskImpl();
		
		for(int i = 0; i < data.size(); i++){
			long rowid = data.get(i);
			impl.read(rowid);
			t[i] = impl.getData().text;
		}
		
		impl.close();
		builder.setItems(t, null);
		builder.setTitle(R.string.tasks);
		builder.setPositiveButton(R.string.ok, null);
		return builder.create();
	}
}
