package com.grsoft.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.ManagerAgent;
import com.grsoft.napoleon.util.FilterAdapter;
import com.grsoft.napoleon.util.FindTextWatcher;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class SelectAgentHelper extends SelectHelper{
	protected List<ManagerAgent> agents = new ArrayList<ManagerAgent>();
	private ManagerAgent selected;
	private AgentSelectedListener agentSelectedListener;
	private Map<String, ManagerAgent> data = new HashMap<String, ManagerAgent>();
	
	interface AgentSelectedListener{
		void onAgentSelected(ManagerAgent agent);
	}
	
	public void setAgentSelectedListner(AgentSelectedListener listener){
		this.agentSelectedListener = listener;
	}
	
	public void init() {
		collectAgents();
	}

	protected void collectAgents() {
		agents.clear();
		data.clear();
		
		DataTraveler.travel(ManagerAgent.class, new DataTraveler.Travel<ManagerAgent>(true) {

			@Override
			public boolean travel(DataTraveler<ManagerAgent> item) {
				agents.add(item.data);
				return true;
			}
		}, null);
		
		Collections.sort(agents, new Comparator<ManagerAgent>(){

			@Override
			public int compare(ManagerAgent lhs, ManagerAgent rhs) {
				return lhs.name.compareTo(rhs.name);
			}});
		
		for(ManagerAgent a : agents)
			if(!data.containsKey(a.id))
				data.put(a.id, a);
	}
	
	public void insertAllAgentsItem(String id, String name){
		ManagerAgent allAgents = new ManagerAgent();
		allAgents.id = id;
		allAgents.name = name;
		
		agents.add(0, allAgents);
	}
	
	public String getAgentName(int index){
		String result = "";
		
		if(index >= 0 && index < agents.size())
			result = agents.get(index).name;
		
		return result;
	}
	
	private static class Adapter extends BaseAdapter implements FilterAdapter{
		private SelectAgentHelper helper;
		private Context context;
		private List<ManagerAgent> data = new ArrayList<ManagerAgent>();
		
		public Adapter(Context context, SelectAgentHelper helper) {
			this.helper = helper;
			this.context = context;
			data.addAll(helper.agents);
		}
		
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
				view = View.inflate(context, R.layout.selectagent_row, null);
			
			ManagerAgent a = (ManagerAgent) getItem(position);
			TextView tv = (TextView) view.findViewById(R.id.tvName);
			tv.setText(a.name);
			
			return view;
		}

		@Override
		public void applyFilter(String value) {
			data.clear();
			
			for(ManagerAgent a : helper.agents) {
				if (a.name.toUpperCase().contains(value.toUpperCase()))
					data.add(a);
			}
			
			notifyDataSetChanged();
		}

		@Override
		public void resetFilter() {
			data.clear();
			data.addAll(helper.agents);
			notifyDataSetChanged();
		}
	}
	
	public Dialog createDialog(Context context){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		View view = View.inflate(context, R.layout.selectagent, null);
		EditText ed = (EditText) view.findViewById(R.id.edFind);
		ListView list = (ListView) view.findViewById(R.id.list);
		View btnClear = view.findViewById(R.id.btnClear);
		FindTextWatcher fw = new FindTextWatcher(ed, list);
		ed.addTextChangedListener(fw);
		
		list.setAdapter(new Adapter(context, this));
		builder.setTitle(R.string.agents);
		builder.setView(view);
		
		final Dialog dlg = builder.create();
		
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ManagerAgent a = (ManagerAgent) parent.getItemAtPosition(position);
				setSelection(a.id);
				dlg.dismiss();
			}
		});
		
		btnClear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText ed = (EditText) dlg.findViewById(R.id.edFind);
				ed.setText("");
			}
		});
		
		return dlg;
	}


	@Override
	protected void applySelect(int which) {
		if(which >= 0 && which < agents.size()){
			selected = agents.get(which);
			
			TextView tv = (TextView)getControl();
			
			if (tv != null)
				tv.setText(selected.name);
			
			fireAgentSelected(selected);
		}
	}
	
	public void prepareDialog(Dialog dialog){
		ListView list = (ListView) dialog.findViewById(R.id.list);
		
		if(list != null) {
			Adapter a = (Adapter) list.getAdapter();
			
			if (a instanceof FilterAdapter)
				((FilterAdapter)a).resetFilter();
			
			EditText ed = (EditText) dialog.findViewById(R.id.edFind);
			ed.setText("");
		}
	}

	public void setSelection(String id) {
		if(id != null)
			for(int i = 0; i < agents.size(); i++ ){
				ManagerAgent a = agents.get(i);
				if(a.id.equals(id)){
					applySelect(i);
					break;
				}
			}
	}
	
	public ManagerAgent getSelected(){ return selected; }
	
	private void fireAgentSelected(ManagerAgent agent){
		if (agentSelectedListener != null)
			agentSelectedListener.onAgentSelected(agent);
	}
	
	public ManagerAgent findAgent(String id){
		if (data.containsKey(id))
			return data.get(id);
		else 
			return null;
	}
}
