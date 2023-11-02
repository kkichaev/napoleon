package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.grsoft.dataobjects.impl.ModifyOrgImpl;
import com.grsoft.dataobjects.impl.NewOrgImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class OrgsListAdapter extends BaseAdapter {
	public Context context;
	public List<Document<?>> data = new ArrayList<Document<?>>();;
	
	public OrgsListAdapter(Context context) {
		this.context = context;
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
	public View getView(int position, View view, ViewGroup parent) {
		if (view == null)
			view = View.inflate(context, R.layout.orgslistrow, null);
		
		OrgInfo org = (OrgInfo) getItem(position);
		
		TextView tv = (TextView) view.findViewById(R.id.tvName);
		tv.setText(org.getName());
		tv.setTextColor(context.getResources().getColor(org.getTextColor()));
		
		tv = (TextView) view.findViewById(R.id.tvAddress);
		tv.setText(org.getAddress());
		tv.setTextColor(context.getResources().getColor(org.getTextColor()));
		
		tv = (TextView) view.findViewById(R.id.tvDate);
		tv.setText(Util.simpleDateFormat.format(org.getDate()));
		tv.setTextColor(context.getResources().getColor(org.getTextColor()));
		
		return view;
	}
	
	public void reload() {
		DocList dl = new DocList(NewOrgImpl.class, null, "created DESC");
		DocList dl2 = new DocList(ModifyOrgImpl.class, null, "created DESC");
		
		data.clear();
		data.addAll(dl.instanceCollections());
		data.addAll(dl2.instanceCollections());
		
		Collections.sort(data, new Comparator<Document<?>>() {

			@Override
			public int compare(Document<?> lhs, Document<?> rhs) {
				CreatableDocument<?> x = (CreatableDocument<?>) lhs;
				CreatableDocument<?> y = (CreatableDocument<?>) rhs;
				
				return y.getData().created.compareTo(x.getData().created);
			}
		});
	}

}
