package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.grsoft.napoleon.util.FilterAdapter;
import com.grsoft.util.view.dialog_helper.KeyValue;


public class RegionAdapter extends BaseAdapter implements FilterAdapter {
	private List<KeyValue> cache = new ArrayList<KeyValue>();
	private List<KeyValue> data = new ArrayList<KeyValue>();
	private Context ctx;
	
	public RegionAdapter(Context ctx, List<KeyValue> data){
		this.cache.addAll(data);
		
		Collections.sort(this.cache, new Comparator<KeyValue>(){
			@Override public int compare(KeyValue lhs, KeyValue rhs) { return lhs.value.toString().compareTo(rhs.value.toString()); }});
		
		this.data.addAll(cache);
		this.ctx = ctx;
	}
	
	@Override
	public int getCount() {	return data.size(); }

	@Override
	public Object getItem(int position) { return data.get(position); }

	@Override
	public long getItemId(int position) { return 0; }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = View.inflate(ctx, R.layout.simple_spinner_layout, null);
			
		KeyValue k = (KeyValue) getItem(position);
		TextView tv = (TextView)convertView.findViewById(R.id.tvFirmaName);
		tv.setText(k.value);
		
		convertView.setBackgroundResource(position % 2 != 0 ? R.drawable.even_row_selector : R.drawable.list_selector);
		
		return convertView;
	}

	@Override
	public void applyFilter(String value) {
		value = value.toUpperCase();
		data.clear();
		
		for(int i = 0; i < cache.size(); i++){
			String v = cache.get(i).value.toString().toUpperCase();
			
			if (v.contains(value))
				data.add(cache.get(i));
		}
		
		notifyDataSetChanged();
	}

	@Override
	public void resetFilter() {
		data.clear();
		data.addAll(cache);
		notifyDataSetChanged();
	}

}
