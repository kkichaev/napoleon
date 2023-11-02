package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.grsoft.dataobjects.DymovTask;
import com.grsoft.dataobjects.DymovTaskResult;
import com.grsoft.dataobjects.impl.DymovTaskImpl;
import com.grsoft.dataobjects.impl.DymovTaskResultImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentsAdapter;
import com.grsoft.napoleon.documents.DymovTaskDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

public class DocumentsEx extends Documents {
	EditText taskText;
	DymovTaskImpl editTask;
	DymovTaskResultImpl taskResult = new DymovTaskResultImpl();
	View dialogLayout;
	
	@Override
	protected boolean canCreateDoc(DocType docType) {
		return docType == DymovTaskDoc.instance() || super.canCreateDoc(docType);
	}
	
	@Override
	protected void createNewDoc() {
		if( DocType.getCurDoc() == DymovTaskDoc.instance() ) {
			if(taskText == null)
				taskText = new EditText(this);
			else
				taskText.setText("");
			showDialog(R.id.add_new_task);
		} else
			super.createNewDoc();
	}
	
	public void openTask(DymovTaskImpl task) {
		editTask = task;
		DymovTaskResult res = taskResult.getData();
		res.idTask = task.getData().idTask;
		if( taskResult.read() && task.getData().isPeriod != 0 ) {
			// reset periodical task result
			if(!Util.isToday(res.done)) {
				res.done = null;
				res.remark = "";
			}
		}
		
		showDialog(R.id.show_task);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		taskResult.close();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == R.id.add_new_task) {
			AlertDialog.Builder bld = new AlertDialog.Builder(this);
			bld.setTitle("Введите задачу");
			taskText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
			bld.setView(taskText);
			
			bld.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
			    @Override public void onClick(DialogInterface dialog, int which) { 
			    	createTask(taskText.getText().toString());
			    	dialog.dismiss();
			    }
			});
			bld.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			    @Override public void onClick(DialogInterface dialog, int which) { dialog.cancel(); }
			});
			
			return bld.create();
		} else if(id == R.id.show_task) {
			return createTaskEditDialog();
		}
		return super.onCreateDialog(id);
	}

	private Dialog createTaskEditDialog() {
		AlertDialog.Builder bld = new AlertDialog.Builder(this);
		bld.setTitle("Выполнение задачи");
		dialogLayout = View.inflate(this, R.layout.task_edit, null);
		bld.setView(dialogLayout);

		bld.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
		    @Override public void onClick(DialogInterface dialog, int which) { 
		    	saveEditTask();
		    	dialog.dismiss();
		    }
		});
		bld.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		    @Override public void onClick(DialogInterface dialog, int which) { dialog.cancel(); }
		});
		
		return bld.create();
	}
	
	protected void saveEditTask() {
		DymovTask dt = editTask.getData();
		DymovTaskResult dtr = taskResult.getData();

		EditText ed = (EditText)dialogLayout.findViewById(R.id.edTaskRemark);
		CheckBox cb = (CheckBox)dialogLayout.findViewById(R.id.cbDone);

		if(cb.isChecked()) {
			dtr.remark = ed.getText().toString();
			dtr.done = Util.getDateTime();
			dtr.id = dt.id;
			dtr.idTask = dt.idTask;
			dtr.flags = 0;
			
			if(!dt.isOwn())
				dtr.created = dtr.done;
			
			taskResult.write();
		}
		adapter.setDocType(DymovTaskDoc.instance());
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
		if(id == R.id.show_task) {
			TextView tv = (TextView)dialogLayout.findViewById(R.id.tvTaskDesc);
			DymovTask dt = editTask.getData();
			String text = "";
			if(dt.isOwn() == false) {
				text += "<b>Автор</b> " + dt.manager + "<br/>";
				text += String.format("<b>Актуальна</b> %s - %s<br/>", 
						Util.simpleDateFormat.format(dt.start), Util.simpleDateFormat.format(dt.date)); 
			} else {
				text += "Собственная<br/>";
			}
			text += "<b>Описание</b><br/>";
			text += dt.task;
			tv.setText(Html.fromHtml(text));
			
			EditText ed = (EditText)dialogLayout.findViewById(R.id.edTaskRemark);
			text = "";
			DymovTaskResult dtr = taskResult.getData();
			if(taskResult.getRowid() != ExtrasConst.INVALID_ROWID)
				text = dtr.remark;
			ed.setText(text);
			CheckBox cb = (CheckBox)dialogLayout.findViewById(R.id.cbDone);
//			cb.setChecked(dtr.done != null && dtr.done.compareTo(dtr.created) >= 0);
			cb.setChecked(false);
		} else
			super.onPrepareDialog(id, dialog, args);
	}

	protected void createTask(String taskText) {
		DymovTaskResultImpl dtr = new DymovTaskResultImpl();
		dtr.createNewTask(org.getData().id, taskText);
		adapter.setDocType(DymovTaskDoc.instance());
	}
	
	@Override
	protected DocumentsAdapter createAdapter(DocType docType, String id) {
		String order = getOrder(docType); 
		return new Adapter(this, docType, id, order);
	}
	
	class Adapter extends DocumentsAdapter {

		public Adapter(Context context, DocType docType, String orgId, String order) {
			super(context, docType, orgId, order);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = super.getView(position, convertView, parent);
			
			if(DocType.getCurDoc() == DymovTaskDoc.instance()){
				DymovTaskImpl doc = (DymovTaskImpl) getItem(position);
				if( doc != null && doc.getData().isOwn() )
					v.setBackgroundResource(R.drawable.own_task_row);
			}
			
			return v;
		}
	}
}
