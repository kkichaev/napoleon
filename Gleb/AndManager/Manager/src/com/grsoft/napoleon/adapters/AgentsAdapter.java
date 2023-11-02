package com.grsoft.napoleon.adapters;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.grsoft.dataobjects.AgentInfo;
import com.grsoft.napoleon.manager.ReportData;

public class AgentsAdapter extends BaseAdapter {

	Context context;
	List<AgentInfo> agents;
	ReportData data;	 
	
	public AgentsAdapter(List<AgentInfo> agents, ReportData data, Context context) {
		this.context = context;
		refresh(agents, data);
	}
	
	public void refresh(List<AgentInfo> agents, ReportData data) {
		this.agents = agents;
		this.data = data;		
		
		notifyDataSetChanged();
	}
	
	@Override public int getCount() { return (agents == null) ? 0 : agents.size(); }
	@Override public Object getItem(int arg0) { return (arg0 < getCount()) ? agents.get(arg0) : null; }
	@Override public long getItemId(int arg0) { return arg0; }

	@Override
	public View getView(int pos, View view, ViewGroup arg2) {
		AgentInfo info = (AgentInfo) getItem(pos);
		return info.getView(context, pos, data);
	}
	
	public void changeCollapseState(long id) {
		
		//for(int i = 0; i<data.)
	}

}
