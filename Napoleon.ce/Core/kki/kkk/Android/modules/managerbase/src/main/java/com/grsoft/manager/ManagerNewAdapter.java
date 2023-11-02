package com.grsoft.manager;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.grsoft.dataobjects.AgentReportData;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.ManagerAgent;
import com.grsoft.napoleon.util.ProgressDrawable;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ManagerNewAdapter extends BaseAdapter {
	private Context context;
	private List<AgentReportData> data = new ArrayList<AgentReportData>();
	private Map<String, ManagerAgent> agents = new HashMap<String, ManagerAgent>();
	private AgentReportData summary; 
	
	public ManagerNewAdapter(Context context) {
		this.context = context;
	}
	
	@Override public int getCount() { return data.size(); }

	@Override public Object getItem(int position) { return data.get(position); }

	@Override public long getItemId(int position) { return 0; }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = View.inflate(context, getViewId(), null);
		adjustView(context, position, view);
		setBackground(position, view);
		return view;
	}
	
	public void load(Date d){
		if(d != null){
			loadAgents();
			loadData(d);
			
			Collections.sort(data, new Comparator<AgentReportData>() {
				@Override
				public int compare(AgentReportData lhs, AgentReportData rhs) {
					String x = agents.containsKey(lhs.id) ? agents.get(lhs.id).name : lhs.id;
					String y = agents.containsKey(rhs.id) ? agents.get(rhs.id).name : rhs.id;
					return x.compareTo(y);
				}
			});
		}
	}

	protected int getViewId() {	return R.layout.agent_row; }
	
	private void loadAgents() {
		agents.clear();
		
		DataTraveler.travel(ManagerAgent.class, new DataTraveler.Travel<ManagerAgent>(true) {

			@Override
			public boolean travel(DataTraveler<ManagerAgent> item) {
				if (!agents.containsKey(item.data.id))
					agents.put(item.data.id, item.data);
				
				return true;
			}
		}, null);
	}

	private void loadData(Date d) {
		data.clear();
		summary = new AgentReportData();
		d = Util.resetTime(d);
		String where = String.format("start_date=%d", d.getTime());
		
		DataTraveler.travel(AgentReportData.class, new DataTraveler.Travel<AgentReportData>(true) {

			@Override
			public boolean travel(DataTraveler<AgentReportData> item) {
				if(agents.containsKey(item.data.id) && agents.get(item.data.id).name.length() > 0) {
					data.add(item.data);
					summary.visits += item.data.visits;
					summary.orders += item.data.orders;
					summary.progress += item.data.progress;
					summary.sum += item.data.sum;
				}
				return true;
			}
		}, where);
		
		int cnt = getCount();
		summary.progress = cnt == 0 ? 0 : summary.progress / cnt;
	}
	
	protected void adjustView(Context context, int position, View view) {
		ManagerAgent agent = null;
		AgentReportData a = (AgentReportData) getItem(position);
		
		if(agents.containsKey(a.id))
			agent = agents.get(a.id);
		
		TextView tv;
		tv = (TextView) view.findViewById(R.id.tvName);
		tv.setText(agent != null ? agent.name : "");

		tv = (TextView) view.findViewById(R.id.tvPhone);
		tv.setText(agent != null ? agent.phone : "");

		tv = (TextView) view.findViewById(R.id.agentSync);
		if (agent != null && agent.date != null && agent.date.getYear() > 100) {
			tv.setText(DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
					DateFormat.MEDIUM, Locale.getDefault()).format(agent.date));
			tv.setVisibility(View.VISIBLE);
		} else
			tv.setVisibility(View.INVISIBLE);

		tv = (TextView) view.findViewById(R.id.tvDistance);

		((TextView) view.findViewById(R.id.tvOrders)).setText(Integer.toString(a.orders));
		((TextView) view.findViewById(R.id.tvVisits)).setText(Integer.toString(a.visits));
		((TextView) view.findViewById(R.id.tvSum)).setText(Util.IntToScaleStr(a.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));

		String progress = a.progress + "%";
		tv = (TextView) view.findViewById(R.id.tvProgress);
		tv.setText(progress);
		tv.setBackgroundDrawable(new ProgressDrawable(a.progress));
		
		view.findViewById(R.id.tvDistance).setVisibility(View.GONE);
	}
	
	protected void setBackground(int pos, View view) {
		view.setBackgroundResource(pos % 2 != 0 ? R.drawable.list_selector
				: R.drawable.even_row_selector);
	}
	
	public void updateSummaryView(Activity activity){
		((TextView) activity.findViewById(R.id.tvOrders)).setText(Integer.toString(summary.orders));
		((TextView) activity.findViewById(R.id.tvVisits)).setText(Integer.toString(summary.visits));
		((TextView) activity.findViewById(R.id.tvSum)).setText(Util.IntToScaleStr(summary.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));

		String progress = summary.progress + "%";
		TextView tv = (TextView) activity.findViewById(R.id.tvProgress);
		tv.setText(progress);
		tv.setBackgroundDrawable(new ProgressDrawable(summary.progress));
	}
}
