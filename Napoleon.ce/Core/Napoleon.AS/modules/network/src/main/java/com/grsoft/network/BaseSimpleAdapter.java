package com.grsoft.network;

import com.grsoft.napoleon.R;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


public abstract class BaseSimpleAdapter extends BaseAdapter {
	@Override public long getItemId(int position) { return 0; }
	
	/***
	 * Вызов базового метода должен идти в конце перегруженного метода 
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView != null){
			convertView.setBackgroundResource(position % 2 != 0 ? R.drawable.even_row_selector : R.drawable.list_selector);
		}
		
		return convertView;
	}
}
