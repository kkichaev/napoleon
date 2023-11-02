package com.grsoft.ads;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.ads.dataobjects.TaskAttachmentInfo;
import com.grsoft.dataobjects.DataTraveler;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TaskAttachmentAdapter extends BaseAdapter {
	private List<TaskAttachmentInfo> data = new ArrayList<TaskAttachmentInfo>();
	private Context context;
	
	public TaskAttachmentAdapter(Context context, String taskid) {
		this.context = context;
		DataTraveler.travel(TaskAttachmentInfo.class, new DataTraveler.Travel<TaskAttachmentInfo>(true) {

			@Override
			public boolean travel(DataTraveler<TaskAttachmentInfo> item) {
				data.add(item.data);
				return true;
			}},String.format("taskid='%s'", taskid));
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
		if (convertView == null)
			convertView = View.inflate(context, R.layout.taskattachmentrow, null);
		
		TaskAttachmentInfo i = (TaskAttachmentInfo) getItem(position);
		TextView tv = (TextView) convertView.findViewById(R.id.tvName);
		tv.setText(i.name);
 		
		return convertView;
	}

}
