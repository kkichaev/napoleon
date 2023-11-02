package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class CheckedAdapter extends BaseAdapter{
	private Context context;
	
	public CheckedAdapter(Context context) {
		this.context = context;
		loadData();
	}
	
	protected List<CheckedItem> data = new ArrayList<CheckedItem>();
	
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
			view = View.inflate(context, R.layout.checked_row, null);
		
		CheckedItem i = (CheckedItem) getItem(position);
		CheckBox cb = (CheckBox) view.findViewById(R.id.cbCheck);
		cb.setOnCheckedChangeListener(null);
		cb.setChecked(i.checked);
		cb.setTag(position);
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				CheckedItem c = (CheckedItem) getItem((Integer) buttonView.getTag()); 
				c.checked = isChecked;
			}
		});
		
		TextView tv = (TextView) view.findViewById(R.id.tvText);
		tv.setText(i.text);
		
		return view;
	}
	
	protected void loadData() {
		Collections.sort(data, new Comparator<CheckedItem>() {
			@Override
			public int compare(CheckedItem lhs, CheckedItem rhs) {
				return lhs.text.compareTo(rhs.text);
			}
		});
	}

	public List<String> getCheckedId() {
		List<String> result = new ArrayList<String>();
		
		for(CheckedItem i : data) {
			if (i.checked)
				result.add(i.id);
		}
		
		return result;
	}

	public void setCheckAll(boolean b) {
		for(CheckedItem i : data) {
			i.checked = b;
		}
	}

	public void setCheckedItems(List<String> id) {
		for(String i : id) {
			for(CheckedItem c : data) {
				if (c.id.equals(i))
					c.checked = true;
			}
		}
	}
}
