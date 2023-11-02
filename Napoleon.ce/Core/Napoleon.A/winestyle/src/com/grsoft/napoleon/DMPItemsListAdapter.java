package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.grsoft.dataobjects.DMPType;
import com.grsoft.dataobjects.VisitItem;
import com.grsoft.dataobjects.VisitItemEx;
import com.grsoft.dataobjects.impl.VisitImpl;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class DMPItemsListAdapter extends BaseAdapter {
	DMPItemsList activity;
	List<DMPType> data = new ArrayList<DMPType>();
	
	public DMPItemsListAdapter(DMPItemsList activity) {
		this.activity = activity;
	}
	
	public void refresh() {
		data.clear();
		
		Set<String> set = new HashSet<String>();
		
		VisitImpl v = activity.getDocument().getRefVisit();
		
		for(VisitItem vi : v.getData().items) {
			VisitItemEx vie = (VisitItemEx) vi;
			
			if(vie.itemId.equals(activity.getPriceID()))
				set.add(vie.dmpId);
		}
		
		for(DMPType d : activity.getDMPTypes()) 
			if (set.contains(d.id))
				data.add(d);
		
		Collections.sort(data, new Comparator<DMPType>() {
			@Override
			public int compare(DMPType lhs, DMPType rhs) {
				return lhs.text.compareTo(rhs.text);
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

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return activity.getRowView(position, convertView, (DMPType)getItem(position));
	}

}
