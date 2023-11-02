package com.grsoft.ads.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import com.grsoft.ads.R;
import com.grsoft.ads.utils.Time;
import com.grsoft.ads.utils.TimeRange;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.napoleon.dataobjects.TaskQuery;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class TaskAdapter extends BaseAdapter {
	private ArrayList<TaskQuery> arr = new ArrayList<TaskQuery>();
	protected Context context;
	
	public TaskAdapter(Context context){
		this.context = context;
	}
	
	public void reload(Date date){
		arr.clear();
		String where = createWhere(date);
		DataTraveler.travel(TaskQuery.class, new DataTraveler.Travel<TaskQuery>(true){
			@Override
			public boolean travel(DataTraveler<TaskQuery> item) {
				arr.add(item.data);
				return true;
			}}, where.toString());
	}
	
	private String createWhere(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		StringBuilder sb = new StringBuilder();
		sb.append("start >= ").append(date.getTime()).append(" and start < ").append(cal.getTime().getTime());
		return sb.toString();
	}

	@Override
	public Object getItem(int arg0) {
		return arr.get(arg0);
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (view == null)
			view = new TaskView(context);
		
		TaskQuery task = (TaskQuery) getItem(position);
		
		TaskView tv = ((TaskView) view); 
		tv.setData(task);
		tv.setBkgColor(getBkgColor(position));
		
		return view;
	}

	private int getBkgColor(int position) {
		int result = context.getResources().getColor(R.color.task_new);
		
		TaskQuery t = (TaskQuery) getItem(position);
		
		if(t.solution == TaskQuery.APPLY)
			result = context.getResources().getColor(R.color.task_apply);
		else if (t.solution == TaskQuery.REJECT)
			result = context.getResources().getColor(R.color.task_rejected);
		else if(t.solution == TaskQuery.INWORK)
			result = context.getResources().getColor(R.color.task_inwork);
		else if(t.solution == TaskQuery.RESOLVED)
			result = context.getResources().getColor(R.color.task_resolved);
		else
			result = context.getResources().getColor(R.color.task_new);
		
		return result;
	}

	@Override
	public int getCount() {	
		return arr.size(); }

	@Override
	public long getItemId(int position) { return 0;	}
	
	public TimeRange getTimeRange(int position){
		TimeRange result = new TimeRange();
		TaskQuery task = (TaskQuery) getItem(position);
		result.start = Time.parse(task.start);
		result.finish = Time.parse(task.finish);
		return result;
	}
}
