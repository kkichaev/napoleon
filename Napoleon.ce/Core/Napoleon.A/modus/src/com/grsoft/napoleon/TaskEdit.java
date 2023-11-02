package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.ATask;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.TaskEnd;
import com.grsoft.dataobjects.TaskItem;
import com.grsoft.dataobjects.impl.TaskEndImpl;
import com.grsoft.util.ExtrasConst;
import com.grsoft.view.BaseActivity;


public class TaskEdit extends BaseActivity {
	private TaskEndImpl doc = new TaskEndImpl();
	private View btnNew;
	private ListView list; 
	private TaskEditAdapter adapter;
	private ATask selTask = null;
	
	public static void open(Context context, long rowid){
		Intent intent = new Intent(context, TaskEdit.class);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.taskedit);
		
		btnNew = findViewById(R.id.btnNew);
		list = (ListView) findViewById(R.id.list);
		
		Intent i = getIntent();
		if(i != null)
			doc.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
			
		btnNew.setOnClickListener(newTaskDlg());
		adapter = new TaskEditAdapter(this);
		list.setAdapter(adapter);
		list.setDividerHeight(0);
		list.setOnItemClickListener(editTaskDlg());
		list.setOnItemLongClickListener(deleteTaskDlg());
		showDialog(R.id.new_task);
	}
	
	private OnItemLongClickListener deleteTaskDlg() {
		return new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				selTask = (ATask) parent.getItemAtPosition(position);
				showDialog(R.id.del_task);
				return true;
			}};
	}

	private OnItemClickListener editTaskDlg() {
		return new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selTask = (ATask) parent.getItemAtPosition(position);
				showDialog(R.id.edit_task);
			}};
	}

	class TaskEditAdapter extends BaseAdapter{
		private List<ATask> data = new ArrayList<ATask>();
		
		private Context context;
		public TaskEditAdapter(Context context){
			this.context = context;
			StringBuilder ids = new StringBuilder();
			
			for(TaskItem i : doc.getData().newtasks){
				if(ids.length() > 0)
					ids.append(", ");
				ids.append("'").append(i.id).append("'");
			}
			
			StringBuilder sb = new StringBuilder();
			sb.append(" taskid in (").append(ids).append(") order by created");
			
			DataTraveler.travel(ATask.class, new DataTraveler.Travel<ATask>(){
				@Override
				public boolean travel(DataTraveler<ATask> item) {
					data.add(item.data);
					item.data = new ATask();
					return true;
				}}, sb.toString());
		}
		
		@Override
		public int getCount() { return data.size();	}

		@Override
		public Object getItem(int position) { return data.get(position); }

		@Override
		public long getItemId(int position) { return 0;	}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if(view == null)
				view = View.inflate(context, R.layout.taskeditrow, null);
			
			if(view != null){
				ATask task = (ATask) getItem(position);
				
				if(task != null){
					TextView tv = (TextView) view.findViewById(R.id.tvText);
					
					if(tv != null)
						tv.setText(task.remark);
				}
				
				view.setBackgroundResource(position % 2 != 0 ? R.drawable.even_row_selector : R.drawable.list_selector);		
			}
			
			return view;
		}

		public void put(ATask task) {
			data.add(task);
			notifyDataSetChanged();
		}
		
	}
	
	private OnClickListener newTaskDlg() {	return new OnClickListener() { @Override public void onClick(View v) { showDialog(R.id.new_task); }	};	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case R.id.new_task:
			return createNewTaskDlg();
		case R.id.edit_task:
			return createEditTaskDlg();
		case R.id.del_task:
			return createDelTaskDlg();
		default:
			return super.onCreateDialog(id);
		}
	}

	private Dialog createDelTaskDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.Del_task_title);
		builder.setMessage(R.string.Confirm_del_task);
		builder.setPositiveButton(R.string.ok, delTask());
		builder.setNegativeButton(R.string.cancel, null);
		return builder.create();
	}

	private android.content.DialogInterface.OnClickListener delTask() {
		return new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(selTask != null){
					StringBuilder sql = new StringBuilder();
					sql.append("delete from ").append(DataObjectInfo.getInstance().getTableName(ATask.class)).append(" where taskid=?");
					String taskid = selTask.taskid;
					
					try{
						DataBaseManager.getDataBase().execSQL(sql.toString(), new String[]{taskid});
					}catch(Exception e){
						e.printStackTrace();
					}
					
					doc.delTask(selTask.taskid);
					adapter.data.remove(selTask);
					adapter.notifyDataSetChanged();
				}
			}
		};
	}

	private Dialog createEditTaskDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.Edit_task_title);
		View view = View.inflate(this, R.layout.taskeditdlg, null);
		builder.setView(view);
		builder.setPositiveButton(R.string.ok, editTask());
		builder.setNegativeButton(R.string.cancel, null);
		return builder.create();
	}

	private android.content.DialogInterface.OnClickListener editTask() {
		return new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(dialog instanceof AlertDialog){
					EditText ed = (EditText) ((AlertDialog)dialog).findViewById(R.id.edText);
					
					if(ed != null){
						String text = ed.getText().toString().trim();
						
						if(text.length() > 0){
							selTask.remark = text;
							DbWriter writer = new DbWriter();
							writer.insertRecord(selTask);
							writer.close();
							doc.write();
							adapter.notifyDataSetChanged();
						}
						
					}
				}
			}
		};
	}

	private Dialog createNewTaskDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.New_task_title);
		View view = View.inflate(this, R.layout.taskeditdlg, null);
		builder.setView(view);
		builder.setPositiveButton(R.string.ok, createNewTask());
		builder.setNegativeButton(R.string.cancel, null);
		return builder.create();
	}

	private android.content.DialogInterface.OnClickListener createNewTask() {
		return new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(dialog instanceof AlertDialog){
					EditText ed = (EditText) ((AlertDialog)dialog).findViewById(R.id.edText);
					
					if(ed != null){
						String text = ed.getText().toString().trim();
						
						if(text.length() > 0){
							TaskEnd te = doc.getData();
							ATask task = new ATask();
							task.taskid = UUID.randomUUID().toString().replace("-", "");
							task.created = te.created;
							task.timeZone = te.timeZone;
							task.remark = text;
							task.id = doc.getId();
							
							DbWriter writer = new DbWriter();
							writer.insertRecord(task);
							writer.close();
							
							if(adapter != null)
								adapter.put(task);
							
							doc.putTask(task.taskid);
							doc.write();
						}
						
					}
				}
			}
		};
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		doc.close();
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch(id){
		case R.id.new_task:
			prepareNewTaskDlg(dialog);
			break;
		case R.id.edit_task:
			prepareEditTaskDlg(dialog);
			break;
		default:
			super.onPrepareDialog(id, dialog);
		}
	}

	private void prepareNewTaskDlg(Dialog dialog) {
		EditText ed = (EditText) dialog.findViewById(R.id.edText);
		
		if(ed != null)
			ed.setText("");
	}

	private void prepareEditTaskDlg(Dialog dialog) {
		EditText ed = (EditText) dialog.findViewById(R.id.edText);
		
		if(ed != null && selTask!= null)
			ed.setText(selTask.remark);
	}
}
