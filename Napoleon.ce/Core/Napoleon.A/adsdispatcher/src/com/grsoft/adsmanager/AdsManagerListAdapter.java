package com.grsoft.adsmanager;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.adsmanager.dataobjects.MAgent;
import com.grsoft.dataobjects.DataTraveler;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdsManagerListAdapter extends BaseAdapter {
	public List<MAgent> data = new ArrayList<MAgent>();
	private Context context;
	
	public AdsManagerListAdapter(Context context) {
		this.context = context;
		refresh();
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
			convertView = View.inflate(context, R.layout.adsmanagerrow, null);
			
		MAgent item = (MAgent) getItem(position);
		TextView tv = (TextView) convertView.findViewById(R.id.tvName);
		tv.setText(item.name);
		
		return convertView;
	}

	public void refresh() {
		data.clear();
		
		DataTraveler.travel(MAgent.class, new DataTraveler.Travel<MAgent>(true) {
			@Override
			public boolean travel(DataTraveler<MAgent> item) {
				if (item.data.hidden == 0)
					data.add(item.data);
				
				return true;
			}
		}, null);
	}
	
}
