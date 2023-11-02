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
				
//				Collections.sort(items, new Comparator<Item>() {
//
//				@Override
//				public int compare(Item lhs, Item rhs) {
//					MonitoringItemImpl left = new MonitoringItemImpl();
//					left.read("id", lhs.item.id);
//					MonitoringItemImpl right = new MonitoringItemImpl();
//					right.read("id", rhs.item.id);
//					
//					return ((MonitoringItemEx)left.getData()).pos - ((MonitoringItemEx)right.getData()).pos;
//				}});
				
				// 11.08.2015 - кабанов просил переделать соритровку по имени
				Collections.sort(items, new Comparator<Item>() {
					@Override public int compare(Item lhs, Item rhs) { return lhs.name.compareTo(rhs.name); }}
				);
			}
			
			/* 24.06.2015 Кабанов просил убрать эти поля */
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
