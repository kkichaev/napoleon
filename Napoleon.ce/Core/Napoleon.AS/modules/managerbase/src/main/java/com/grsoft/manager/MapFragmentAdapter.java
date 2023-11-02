package com.grsoft.manager;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.Org;
import com.grsoft.manager.MapData.Executed;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MapFragmentAdapter extends BaseAdapter {
	private Context context;
	private List<MapData.Executed> data;
	public MapFragmentAdapter(Context context, MapData mapData) {
		this.data = new ArrayList<MapData.Executed>();
		this.context = context;
		
		for(Object o : mapData.executed)
			data.add((Executed) o);
	}

	@Override public int getCount() { return data.size(); }

	@Override public Object getItem(int position) { return data.get(position); }

	@Override public long getItemId(int position) { return 0; }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null)
			convertView = View.inflate(context, R.layout.map_fragment_new_row, null);
		
		MapData.Executed e = (Executed) getItem(position);
		
		TextView tv = (TextView) convertView.findViewById(R.id.tvIdx);
		tv.setText(Integer.toString(e.idx));
		
		tv = (TextView) convertView.findViewById(R.id.tvName);
		tv.setText(Html.fromHtml(orgText(e.org)));
		
		setBackground(position, convertView);
		
		return convertView;
	}

	private String orgText(Org org) {
		return "<b>" + org.name + "</b><br><i>" + org.address+"</i>";
	}
	
	protected void setBackground(int pos, View view) { view.setBackgroundResource(pos % 2 != 0 ? R.drawable.list_selector : R.drawable.even_row_selector); }
}
