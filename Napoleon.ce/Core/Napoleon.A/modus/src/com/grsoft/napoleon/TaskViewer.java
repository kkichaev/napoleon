package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.ATask;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.TaskAnswer;
import com.grsoft.dataobjects.impl.TaskBeginImpl;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;


public class TaskViewer extends BaseActivity {
	private ListView list;
	private TaskBeginImpl doc = new TaskBeginImpl();
	
	public static void open(Context context, long rowid){
		Intent intent = new Intent(context, TaskViewer.class);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(intent);		
	}
	
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		list = new ListView(this);
		
		Intent i = getIntent();
		if(i != null){
			doc.read(i.getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
			doc.close();
		}
			
		list.setBackgroundColor(getResources().getColor(R.color.white));
		setContentView(list);
		list.setAdapter(new TaskViewAdapter());
	};
	
	class TaskViewAdapter extends BaseAdapter{
		List<ATask> data = new ArrayList<ATask>();
		
		public TaskViewAdapter(){
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
				view = View.inflate(TaskViewer.this, R.layout.taskviewrow, null);
			
			ATask task = (ATask) getItem(position);
			int color = getResources().getColor(task.manager == 0 ? R.color.black : R.color.red);
			
			if(task != null){
				TextView tv = (TextView) view.findViewById(R.id.tvDate);
				if(tv != null){
					tv.setText(Util.simpleDateFormat.format(task.created));
					tv.setTextColor(color);
				}
				
				tv = (TextView) view.findViewById(R.id.tvText);
				
				if(tv != null){
					tv.setText(task.remark);
					tv.setTextColor(color);
				}
			}
			
			return view;
		}
		
	}
}
