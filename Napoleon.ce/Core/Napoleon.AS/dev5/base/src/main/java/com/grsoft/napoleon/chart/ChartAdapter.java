package com.grsoft.napoleon.chart;
import com.grsoft.aceteam.R;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ChartAdapter extends BaseAdapter {
	private final static int CHART_TYPE_COUNT = 3;
	private List<ChartView> data = new ArrayList<ChartView>();
	
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
		return ((ChartView)getItem(position)).getView(convertView);
	}
	
	@Override
	public int getViewTypeCount() {
		return CHART_TYPE_COUNT;
	}
	
	@Override
	public int getItemViewType(int position) {
		return ((ChartView)getItem(position)).getViewType();
	}

	public void addView(ChartView view) {
		data.add(view);
	}
	
}
