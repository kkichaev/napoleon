package com.grsoft.ads;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


public abstract class ListBaseAdapter extends BaseAdapter {
	protected Context context; 
	protected abstract int getLayoutID();
	protected abstract int initView(View view, Object item);
	
	public ListBaseAdapter(Context context){
		this.context = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = View.inflate(context, getLayoutID(), null);
		
		Object item = getItem(position);
		initView(convertView, item);
		setBackground(convertView, item, position);
		
		return convertView;
	}
	
	protected void setBackground(View view, Object item, int position) {
		view.setBackgroundResource(position % 2 != 0 ? R.drawable.even_row_selector : R.drawable.list_selector);
	}
}
