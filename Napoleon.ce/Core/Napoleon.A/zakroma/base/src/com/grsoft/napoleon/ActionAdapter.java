package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.grsoft.dataobjects.Action;
import com.grsoft.dataobjects.DataTraveler;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ActionAdapter extends BaseAdapter {
	private ActionView activity;
	private List<Action> data = new ArrayList<Action>();
	
	public ActionAdapter(ActionView activity) {
		this.activity = activity;
		loadData();
	}
	
	private void loadData() {
		data.clear();
		
		DataTraveler.travel(Action.class, new DataTraveler.Travel<Action>(true) {

			@Override
			public boolean travel(DataTraveler<Action> item) {
				data.add(item.data);
				return true;
			}}, null);
		
		Collections.sort(data, new Comparator<Action>() {

			@Override
			public int compare(Action lhs, Action rhs) {
				return lhs.name.compareTo(rhs.name);
			}
		});
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override public long getItemId(int position) { return 0; }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = View.inflate(activity, activity.getRowLayout(), null);
		
		activity.bindViewRow(convertView, getItem(position));
		return convertView;
	}

}
