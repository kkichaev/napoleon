package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrgMatrix;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class DMPAdapter extends BaseAdapter {
	List<OrgMatrix> data = new ArrayList<OrgMatrix>();
	DMPEdit activity;
	
	public DMPAdapter(DMPEdit activity, String id) {
		this.activity = activity;
		
		DataTraveler.travel(OrgMatrix.class, new DataTraveler.Travel<OrgMatrix>(true) {

			@Override
			public boolean travel(DataTraveler<OrgMatrix> item) {
				data.add(item.data);
				return true;
			}
		}, "id='" + id + "'");
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
			convertView = View.inflate(activity, R.layout.dmpeditrow, null);
		return activity.getViewRow(convertView, position, (OrgMatrix) getItem(position));
	}

}
