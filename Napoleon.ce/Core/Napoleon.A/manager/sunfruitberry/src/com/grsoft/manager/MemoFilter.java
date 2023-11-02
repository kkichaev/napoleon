package com.grsoft.manager;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

public class MemoFilter extends DialogFragment {
	
	Action handler;
	
	public interface Action {
		void selected(int index);
	}
	
	public void setHandler(Action handler) { this.handler = handler; }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().setTitle(R.string.memo_filter_title);
		View v = inflater.inflate(R.layout.memo_filter, container);
		ListView lv = (ListView)v.findViewById(R.id.lvItems);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(handler != null)
					handler.selected(arg2);
				dismiss();
			}
		});
		return v;
	}
}
