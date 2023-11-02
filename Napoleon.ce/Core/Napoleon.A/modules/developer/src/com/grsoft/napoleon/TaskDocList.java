package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgTask;
import com.grsoft.dataobjects.TaskDone;
import com.grsoft.dataobjects.impl.OrgTaskExecImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentUtils;
import com.grsoft.napoleon.documents.TaskDoneDocW;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.util.gps.GPSUtilNew;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TaskDocList extends DocumentsBase {
	public static Class<?> activity = TaskDocList.class;
	private static final String HIDE_DOC_LIST = "HIDE_DOC_LIST";

	HashMap<String, TaskDone> taskDone = new HashMap<String, TaskDone>();
	
	OrgTask editRemark;
	boolean hideList = false;
	
	static public void open(Context context, Org org) {
		open(context, org, false);
	}
	static public void open(Context context, Org org, boolean hideDoceList) {
		Intent i = new Intent(context, activity);
		
		i.putExtra(ExtrasConst.ORG_ID_STR, org.id);
		i.putExtra(HIDE_DOC_LIST, hideDoceList);
		context.startActivity(i);		
	}
	
	@Override
	protected void adjustViewForDocType(DocType docType) {
		if( !hideList && docType != TaskDoneDocW.instance()) {
			DocumentsW.open(this, org.getData());
			finish();
		}
		super.adjustViewForDocType(docType);
	}
	
	@Override
	protected void refreshTotalSum() {
		findViewById(R.id.tvTotalSum).setVisibility(View.GONE);
	}
	
	@Override
	protected void init(Bundle b) {
		super.init(b);
		
		btnNewDoc.setVisibility(View.GONE);
		hideList = b.getBoolean(HIDE_DOC_LIST, false);
		if( hideList )
			findViewById(R.id.btnDocFilter).setVisibility(View.GONE);
		
		
		TextView tv;
		tv = (TextView) findViewById(R.id.tvMainDocValColTitle);
		if (tv != null)
			tv.setText(R.string.task_title);
		
		tv = (TextView)findViewById(R.id.SumColumnTitle);		
		if (tv != null)
			tv.setVisibility(View.GONE);
	}
	
	public String getRemark(String idtask) {
		String result = "";
		
		if (taskDone.containsKey(idtask))
			result = taskDone.get(idtask).remark;
		
		return result;
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if( id == R.id.edEditRemark ) {
			EditText ed = (EditText) dialog.findViewById(R.id.edRemark);
			ed.setText(getRemark(editRemark.id));
		} else
			super.onPrepareDialog(id, dialog);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == R.id.edEditRemark) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setView(View.inflate(this, R.layout.input_remark, null));
			builder.setTitle(R.string.message);
			builder.setPositiveButton(R.string.ok, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					saveTaskRemark(((EditText) ((AlertDialog) dialog).findViewById(R.id.edRemark)).getText().toString());
				}
			});

			builder.setNegativeButton(R.string.cancel, null);
			return builder.create();
		}
		return super.onCreateDialog(id);
	}
	
	protected void saveTaskRemark(String remark) {
		TaskDone td = taskDone.get(editRemark.id);
		if( td == null ) {
			OrgTaskExecImpl doc = new OrgTaskExecImpl(); 
			doc.init(this, editRemark, GPSUtilNew.getLastKnownLocation(this));
			taskDone.put(editRemark.id, doc.getData());
			td = doc.getData();
		}
		
		if( DocumentUtils.isExported(td.params) == false ) {
			td.remark = remark;
			if(td.items.size() > 0) 
				td.items.get(0).text = remark;
			DbWriter w = new DbWriter();
			w.insertRecord(td);
			w.close();
			
			((BaseAdapter)((ListView)findViewById(R.id.lvDocs)).getAdapter()).notifyDataSetChanged();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		DataTraveler.travel(TaskDone.class, new DataTraveler.Travel<TaskDone>() {

			@Override
			public boolean travel(DataTraveler<TaskDone> item) {
				taskDone.put(item.data.idTask, item.data);
				item.data = new TaskDone();
				return true;
			}
		}, "id = '" + org.getData().id + "'");
		
		ListView lv = (ListView)findViewById(R.id.lvDocs);
		lv.setDividerHeight(0);
		lv.setAdapter(createAdapter());
		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				editRemark = (OrgTask)arg0.getItemAtPosition(arg2);
				showDialog(R.id.edEditRemark);
				return true;
			}
		});
	}
	
	protected Adapter createAdapter() {
		return new Adapter();
	}
	
	protected boolean isTaskChecked(OrgTask task) { return taskDone.containsKey(task.id); }
	
	protected int compareTask(OrgTask l, OrgTask r) {
		TaskDone ld = taskDone.get(l.id);
		TaskDone rd = taskDone.get(r.id);
		
		if( ld == null && rd != null )
			return -1;
		
		if( rd == null && ld != null )
			return 1;
		
		return l.finish.compareTo(r.finish);
	}
	
	protected void checkTask(OrgTask task, CheckBox cb) {
		TaskDone td = taskDone.get(task.id);
		if( cb.isChecked() ) {
			if(td == null) {
				OrgTaskExecImpl doc = new OrgTaskExecImpl(); 
				doc.init(this, task, GPSUtilNew.getLastKnownLocation(this));
				taskDone.put(task.id, doc.getData());
			}
		} else {
			if( td != null && DocumentUtils.isExported(td.params) == false ) {
				taskDone.remove(task.id);
				((BaseAdapter)((ListView)findViewById(R.id.lvDocs)).getAdapter()).notifyDataSetChanged();
				
				try {
					String sql = "DELETE FROM " + td.getTableName() + " WHERE created = " + Long.toString(td.created.getTime());
					DataBaseManager.getDataBase().execSQL(sql);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				cb.setChecked(true);
				Toast.makeText(this, R.string.task_sended, Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	class Adapter extends BaseAdapter {
		
		List<OrgTask> data = new ArrayList<OrgTask>();		
		
		public Adapter() {
			refresh();
		}
		
		public void refresh() {
			String now = Long.toString(Util.getDate().getTime());
			String orgId = org.getData().id;
			String where = "orgid = '" + orgId + "' and (start <= " + now + 
					" and finish >= " + now + " or not id in (select idTask from " +
					new TaskDone().getTableName() + " where id = '" + orgId + "'))";
			DataTraveler.travel(OrgTask.class, new DataTraveler.Travel<OrgTask>() {

				@Override
				public boolean travel(DataTraveler<OrgTask> item) {
					data.add(item.data);
					item.data = new OrgTask();
					return true;
				}
			}, where);
			
			Collections.sort(data, new Comparator<OrgTask>() {
				@Override public int compare(OrgTask lhs, OrgTask rhs) { return compareTask(lhs, rhs); }
			});
		}

		@Override public int getCount() { return data.size(); }
		@Override public Object getItem(int arg0) { return arg0 < data.size() ? data.get(arg0) : null; }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			if( view == null )
				view = View.inflate(TaskDocList.this, R.layout.org_task_row, null);
			
			final OrgTask item = (OrgTask) getItem(arg0);
			if( item != null ) {
				TextView tv;
				String text;
				tv = (TextView)view.findViewById(R.id.tvTask);
				
				text = item.text;
				String remark = getRemark(item.id);
				if( remark.length() > 0) {
					text += "<br/><i>" + remark + "<i>";
				}
				tv.setText(Html.fromHtml(text));
				
				SimpleDateFormat sdf = new SimpleDateFormat("dd.MM");
				text = sdf.format(item.start) + " - " + sdf.format(item.finish);
				tv = (TextView)view.findViewById(R.id.tvDate);
				tv.setText(text);
				
				CheckBox cb;
				cb = (CheckBox)view.findViewById(R.id.cbTaskDone);
				cb.setChecked(isTaskChecked(item));
				
				cb.setOnClickListener(new View.OnClickListener() {
					@Override public void onClick(View v) { checkTask(item, (CheckBox)v); }
				});
			}
			
			view.setBackgroundResource((arg0 % 2) != 0 ? R.drawable.even_row_selector
					: R.drawable.list_selector);
			
			return view;
		}

		
		
	}
}
