package com.grsoft.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Question;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AuditAdapter extends BaseAdapter {
	private List<Question> data = new ArrayList<Question>();
	private Context context;
	
	public AuditAdapter(Context context) {
		this.context = context;
		reload();
	}

	public void reload() {
		data.clear();
		DataTraveler.travel(Question.class, new DataTraveler.Travel<Question>(true){

			@Override
			public boolean travel(DataTraveler<Question> item) {
				data.add(item.data);
				return true;
			}}, null);
		
		Collections.sort(data, new Comparator<Question>() {

			@Override
			public int compare(Question lhs, Question rhs) {
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

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		
		if (view == null)
			view = View.inflate(context, R.layout.audit_row, null);
		
		Question item = (Question) getItem(position);
		TextView tv = (TextView) view.findViewById(R.id.tvName);
		
		tv.setText(item.name);
		
		return view;
	}

}
