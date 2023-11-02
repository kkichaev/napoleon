package com.grsoft.napoleon;

import java.util.Collections;
import java.util.Comparator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


public class MonitoringEditEx extends MonitoringEdit {
	protected void applayAdapter() {
		ListView listView = (ListView)findViewById(R.id.lvItems);
		listView.setAdapter(new Adapter(){
			@Override
			protected void buildData() {
				super.buildData();
				Collections.sort(items, new Comparator<_MntrItem>() {
					@Override public int compare(_MntrItem lhs, _MntrItem rhs) { return lhs.name.compareTo(rhs.name); }}
				);
			}
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View result = super.getView(position, convertView, parent);
				
				if(result != null){
					result.findViewById(R.id.tvFace).setVisibility(View.GONE);
					result.findViewById(R.id.tvSKU).setVisibility(View.GONE);
				}
				
				return result;
			}
		});
	}
}
