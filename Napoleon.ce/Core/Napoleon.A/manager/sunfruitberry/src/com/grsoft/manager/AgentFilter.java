package com.grsoft.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.grsoft.dataobjects.ManagerAgent;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class AgentFilter extends DialogFragment {
	Action handler;
	
	public interface Action {
		void selected(ManagerAgent agent);
	}
	
	public void setHandler(Action handler) { this.handler = handler; }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().setTitle(R.string.agent_filter);
		View v = inflater.inflate(R.layout.agent_filter, container);
		ListView lv = (ListView)v.findViewById(R.id.lvItems);
		lv.setAdapter(new Adapter());
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(handler != null) {
					ManagerAgent a = (ManagerAgent) arg0.getAdapter().getItem(arg2);
					handler.selected(a);
				}
				dismiss();
			}
		});
		return v;
	}
	
	class Adapter extends BaseAdapter {
		List<ManagerAgent> agents = new ArrayList<ManagerAgent>();
		
		public Adapter() {
			agents.addAll(ManagerAgent.getAgents().values());
			Collections.sort(agents);
			ManagerAgent allA = new ManagerAgent();
			allA.name = "<Все>";
			agents.add(0, allA);
		}

		@Override public int getCount() { return agents.size(); }
		@Override public Object getItem(int arg0) { return agents.get(arg0); }
		@Override public long getItemId(int arg0) { return 0; }

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			if(view == null)
				view = View.inflate(getActivity(), R.layout.agent_filter_row, null);
			
			ManagerAgent a = (ManagerAgent) getItem(arg0);
			TextView tv = (TextView)view.findViewById(R.id.tvName);
			tv.setText(a.name);
			return view;
		}
	}
}
