package com.grsoft.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AgentsInFieldsAdapter extends BaseAdapter {
	private List<MapData.AgentInField> data = new ArrayList<MapData.AgentInField>();
	private Context context;
	private final SimpleDateFormat sdf =  new SimpleDateFormat("HH:mm", Locale.getDefault());
	
	public AgentsInFieldsAdapter(Context context, List<MapData.AgentInField> data){
		this.context = context;
		this.data.addAll(data);
	}
	
	@Override public int getCount() { return data.size(); }
	@Override public Object getItem(int position) { return data.get(position); }
	@Override public long getItemId(int position) { return 0; }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null)
			convertView = View.inflate(context, R.layout.agents_in_fields_row, null);
		
		MapData.AgentInField e = (MapData.AgentInField) getItem(position);
		
		TextView tv = (TextView) convertView.findViewById(R.id.tvIdx);
		tv.setText(Integer.toString(e.idx));
		
		tv = (TextView) convertView.findViewById(R.id.tvName);
		tv.setText(e.agent.name);
		
		tv = (TextView) convertView.findViewById(R.id.tvTime);
		tv.setText(sdf.format(e.date));
		
		setBackground(position, convertView);
		
		return convertView;
	}

	protected void setBackground(int pos, View view) { view.setBackgroundResource(pos % 2 != 0 ? R.drawable.list_selector : R.drawable.even_row_selector); }
}
