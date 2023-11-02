package com.grsoft.ads;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.napoleon.dataobjects.TaskQuery;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdsNewAdapter extends BaseAdapter {
	private AdsNew context;
	private ArrayList<TaskQuery> data = new ArrayList<TaskQuery>();
	private TaskTimeHelper tth = new TaskTimeHelper();
	
	public AdsNewAdapter(AdsNew adsNew) {
		this.context = adsNew;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if(convertView == null)
			convertView = View.inflate(context, R.layout.main_row, null);
		
		TaskQuery item = (TaskQuery) getItem(position);
		
		TextView tv = (TextView) convertView.findViewById(R.id.tvText);
		
		tv.setTypeface(null, item.solution == TaskQuery.NEW ? Typeface.BOLD : Typeface.NORMAL);
		tv.setText(item.text);
		
		tv = (TextView) convertView.findViewById(R.id.tvTime);
		tv.setText(timeToStr(item.start, item.finish));
		
		tv = (TextView) convertView.findViewById(R.id.tvStatus);
		updateStatus(tv, item.solution);
		
		return convertView;
	}
	
	private void updateStatus(TextView tv, int solution) {
		//Log.d("ADS", String.format("Update status: %d", solution));
		tv.setCompoundDrawablesWithIntrinsicBounds(null, 
				getStatusDrawable(solution), null, null);
		tv.setText(getStatusText(solution));
	}

	private String getStatusText(int solution) {
		int id = R.string.status_new;
		
		if(solution == TaskQuery.APPLY)
			id = R.string.status_apply;
		else if (solution == TaskQuery.INWORK)
			id = R.string.status_inwork;
		else if (solution == TaskQuery.REJECT)
			id = R.string.status_reject;
		else if (solution == TaskQuery.RESOLVED)
			id = R.string.status_resolved;
			
		return context.getResources().getString(id);
	}

	private String timeToStr(Date start, Date finish) {
		return tth.timeToString(start, finish);
	}

	public void reload(Date date) {
		Log.d("ADS", "reload");
		data.clear();
		
		String where = createWhere(date);
		DataTraveler.travel(TaskQuery.class, new DataTraveler.Travel<TaskQuery>(true){
			@Override
			public boolean travel(DataTraveler<TaskQuery> item) {
				data.add(item.data);
				Log.d("ADS", String.format("reload status: %d", item.data.solution));
				return true;
			}}, where.toString());
		
		
		Collections.sort(data, new Comparator<TaskQuery>() {

			@Override
			public int compare(TaskQuery lhs, TaskQuery rhs) {
				return (int)(lhs.start.getTime() - rhs.start.getTime());
			}
		});
		
		notifyDataSetChanged();
	}
	
	private String createWhere(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		StringBuilder sb = new StringBuilder();
		sb.append("start <= ").append(cal.getTime().getTime()).append(" and finish >= ").append(date.getTime());
		return sb.toString();
	}
	
	private Drawable getStatusDrawable(int solution) {
		int id = R.drawable.ic_new; 
		
		if(solution == TaskQuery.APPLY)
			id = R.drawable.ic_apply;
		else if (solution == TaskQuery.INWORK)
			id = R.drawable.ic_inwork;
		else if (solution == TaskQuery.REJECT)
			id = R.drawable.ic_clear_black_48dp;
		else if (solution == TaskQuery.RESOLVED)
			id = R.drawable.ic_done_black_48dp;
		
		return context.getResources().getDrawable(id);
	}
}
