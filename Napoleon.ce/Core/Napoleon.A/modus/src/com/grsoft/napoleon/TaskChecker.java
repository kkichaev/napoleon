package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.ATask;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.TaskAnswer;
import com.grsoft.dataobjects.TaskEnd;
import com.grsoft.dataobjects.TaskItem;
import com.grsoft.dataobjects.impl.TaskEndImpl;
import com.grsoft.util.ExtrasConst;
import com.grsoft.view.BaseActivity;

public class TaskChecker extends BaseActivity {
	private ListView list;
	private TaskEndImpl doc = new TaskEndImpl();
	private List<String> exec = new ArrayList<String>();
	
	public static void open(Context context, long rowid){
		Intent intent = new Intent(context, TaskChecker.class);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(intent);
	}
	
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		list = new ListView(this);
		list.setBackgroundColor(getResources().getColor(R.color.white));
		setContentView(list);
		
		Intent i = getIntent();
		if(i != null){
			doc.read(i.getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
			
			for(TaskItem ti : doc.getData().exectasks)
				exec.add(ti.id);
		}
		
		list.setAdapter(new TaskCheckerAdapter());
	};
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case R.id.ask_to_create_task:
			return createAskToCreateTask();
		default:
			return super.onCreateDialog(id);
		}
	}
	
	private Dialog createAskToCreateTask() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.question);
		builder.setMessage(R.string.New_task_msg);
		builder.setPositiveButton(R.string.ok, new OnClickListener() { @Override public void onClick(DialogInterface dialog, int which) { 
				TaskEdit.open(TaskChecker.this, doc.getRowid());
				finish();
			}});
		builder.setNegativeButton(R.string.cancel, new OnClickListener() { @Override public void onClick(DialogInterface dialog, int which) { finish(); } });
		return builder.create();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		doc.close();
	}

	@Override
	public void onBackPressed() {
		if(doc.isEditable())
			showDialog(R.id.ask_to_create_task);
		else
			super.onBackPressed();
	}
	
	class TaskCheckerAdapter extends BaseAdapter{
		List<ATask> data = new ArrayList<ATask>();
		
		public TaskCheckerAdapter(){
			Class<? extends DataObject> type = TaskAnswer.class;
			
			DbWriter.checkDBTable(type);
			StringBuilder where = new StringBuilder("not taskid in (select taskid from ").append(DataObjectInfo.getInstance().getTableName(type)).append(" )");
			where.append(" and id ='").append(doc.getId()).append("'");
			DataTraveler.travel(ATask.class, new DataTraveler.Travel<ATask>(){
				@Override
				public boolean travel(DataTraveler<ATask> item) {
					data.add(item.data);
					item.data = new ATask();
					return true;
				}
				
			}, where.toString());
		}
		
		@Override
		public int getCount() {	return data.size();	}

		@Override
		public Object getItem(int position) { return data.get(position); } 

		@Override
		public long getItemId(int position) { return 0;	}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null)
				view = View.inflate(TaskChecker.this, R.layout.taskcheckrow, null);
			
			ATask task = (ATask) getItem(position);
			
			if(task != null){
				int color = getResources().getColor(task.manager == 0 ? R.color.black : R.color.red);
				CheckBox cb = (CheckBox) view.findViewById(R.id.cbTask);
				if(cb != null){
					cb.setTag(task.taskid);
					
					if(exec.contains(task.taskid))
						cb.setChecked(true);
					else
						cb.setChecked(false);
					
					if(doc.isEditable())
						cb.setOnCheckedChangeListener(taskstatus);
					
					cb.setText(task.remark);
					cb.setTextColor(color);
				}
			}
			
			return view;
		}
	}
	
	OnCheckedChangeListener taskstatus = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			String taskid = (String) buttonView.getTag();
			
			if(taskid != null){
				if(isChecked){
					TaskAnswer answer = new TaskAnswer();
					TaskEnd te = doc.getData();
					answer.created = te.created;
					answer.taskid = taskid; 
					answer.id = te.id;
					answer.timeZone = te.timeZone;
					
					DbWriter writer = new DbWriter();
					writer.insertRecord(answer);
					writer.close();
					
					doc.execTask(taskid);
					exec.add(taskid);
					doc.write();
				}else{
					DataBaseManager.getDataBase().execSQL("delete from " + DataObjectInfo.getInstance().getTableName(TaskAnswer.class) + " where taskid = ?", new String[]{taskid});
					doc.delExecTask(taskid);
					exec.remove(taskid);
				}
			}
		}
	};
}
