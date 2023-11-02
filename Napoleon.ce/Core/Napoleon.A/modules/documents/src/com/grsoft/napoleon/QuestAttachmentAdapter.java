package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class QuestAttachmentAdapter extends BaseAdapter {
	private List<QuestionAttachInfo> data = new ArrayList<QuestionAttachInfo>();
	private Context context;
	
	public QuestAttachmentAdapter(Context context, ArrayList<? extends Parcelable> attaches) {
		this.context = context;
		
		for(Object o : attaches)
			data.add((QuestionAttachInfo) o);
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
		
		QuestionAttachInfo i = (QuestionAttachInfo) getItem(position);
		TextView tv = (TextView) convertView.findViewById(R.id.tvName);
		tv.setText(i.name);
 		
		return convertView;
	}

}
