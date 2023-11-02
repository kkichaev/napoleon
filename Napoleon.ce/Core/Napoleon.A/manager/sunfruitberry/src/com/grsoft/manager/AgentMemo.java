package com.grsoft.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grsoft.database.DataObjectSendHitching;
import com.grsoft.database.DbWriter;
import com.grsoft.database.Hitching;
import com.grsoft.database.ReportHitching;
import com.grsoft.dataobjects.AgentManagerMemo;
import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.ManagerAgent;
import com.grsoft.dataobjects.ManagerMemo;
import com.grsoft.dataobjects.MemoProceeded;
import com.grsoft.dataobjects.ReportOnAgentForDatesParams;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.network.ObjectListener;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class AgentMemo extends DrawerActivity implements MemoFilter.Action, ManagerMemoDialog.Action, AgentFilter.Action {

	AgentMemoAdapter adapter;
	static Map<String, String> topics;
	
	public static void open(Context context){
		Intent i = new Intent(context, AgentMemo.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) ;
		context.startActivity(i);
	}

	@Override protected String getActionBarTitle() { return getString(R.string.agent_memo); }
	@Override protected int getLayoutID() { return R.layout.agent_memo; }
	@Override protected int getOptionsMenuID() { return R.menu.main_menu_ex; }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ListView list = (ListView) findViewById(R.id.list);
		adapter = new AgentMemoAdapter();
		list.setAdapter(adapter);
		list.setDividerHeight(0);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				AgentManagerMemo data = (AgentManagerMemo) arg0.getAdapter().getItem(arg2);
				ManagerMemoDialog dlg = new ManagerMemoDialog();
				dlg.setData(data);
				dlg.setHandler(AgentMemo.this);
				dlg.show(getSupportFragmentManager(), "");
			}
		});
	}
	
	public static String getTopic(String code) {
		if(topics == null) {
			topics = new HashMap<String, String>();
			ConfigImpl ci = new ConfigImpl();
			Config c = ci.getData();
			c.key = "ТемыСлужебныхЗаписок";
			ci.read();
			ci.close();
			
			for(String kv : c.value.split(";")) {
				String[] data = kv.split("\t");
				if(data.length == 2)
					topics.put(data[1], data[0]);
			}
		}

		String ret = topics.get(code);
		return ret == null ? code : ret; 
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int iid = item.getItemId(); 
		if(iid == R.id.itFilter) {
			MemoFilter f = new MemoFilter();
			f.setHandler(this);
			f.show(getSupportFragmentManager(), "");
			return true;
		} else if(iid == R.id.itAgentFilter) {
			AgentFilter f = new AgentFilter();
			f.setHandler(this);
			f.show(getSupportFragmentManager(), "");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void selected(int index) {
		adapter.filterColor(index);
	}
	
	@Override
	public void accept(final AgentManagerMemo data, boolean accept) {
		if(data.setAllowed(accept)) {
			List<ObjectListener> send = new ArrayList<ObjectListener>();
			
			MemoProceeded mp = new MemoProceeded(data);
			DataObjectSendHitching sh = new DataObjectSendHitching(mp, "OrderProceeded");
			send.add(sh);
			
			if(data.isAllowed()) {
				ManagerMemo mm = new ManagerMemo(data);
				sh = new DataObjectSendHitching(mm, "ManagerMemo");
				send.add(sh);
			}
			
			List<Hitching> ret = new ArrayList<Hitching>();
			UpdateProcess upp = new UpdateProcess(this, new UpdateCtrl() {
				@Override public void updateCtrl(boolean enabled) {}
				
				@Override
				public void onFinish(boolean success) {
					if( success ) {
						DbWriter wr = new DbWriter();
						wr.insertRecord(data);
						wr.close();
					}
				}
			}, ret);
			upp.setSending(send);
			upp.execute((Void[]) null);
			
			adapter.notifyDataSetChanged();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		adapter.refresh();
	}
	
	@Override
	protected void postSyncUpdate() {
		topics = null;
		adapter.refresh();
	}

	@Override
	protected void initHitchings(List<Hitching> list) {
		List<Hitching> repResult = new ArrayList<Hitching>();
		repResult.add(new Hitching(AgentManagerMemo.class, "AgentManagerMemo", true));

		list.add(new Hitching(Config.class, "Config", false));		
		list.add(new ReportHitching("agent_memo", new ReportOnAgentForDatesParams(new Date(), new Date()), repResult));
	}
	
	class AgentMemoAdapter extends BaseAdapter {

		String where;
		Map<String, ManagerAgent> agents;
		List<AgentManagerMemo> data = new ArrayList<AgentManagerMemo>();
		
		int curColor = 0;
		String curAgent = "";
		
		public AgentMemoAdapter() {
			agents = ManagerAgent.getAgents();
			where = "";
		}
		
		public void refresh() {
			data.clear();
			
			String cwhere = colorWhere(curColor);
			String agwhere = agentWhere(curAgent);
			where = cwhere;
			if(agwhere.length() > 0) {
				if(where.length() > 0)
					where += " and ";
				where += agwhere;
			}
			
			
			DataTraveler.travel(AgentManagerMemo.class, new DataTraveler.Travel<AgentManagerMemo>(true) {

				@Override
				public boolean travel(DataTraveler<AgentManagerMemo> item) {
					data.add(item.data);
					return true;
				}
			}, where);
			Collections.sort(data);
			notifyDataSetChanged();
		}

		String colorWhere(int index) {
			String newWhere = "";
			if(index == 1) 
				newWhere = "dogColor = -65536";
			else if(index == 2)
				newWhere = "dogColor = -16776961";
			else if(index == 3)
				newWhere = "dogColor = -16751616"; // FF006400
			return newWhere;
		}
		
		String agentWhere(String uid) {
			return uid.length() == 0 ? "" : "userid = '" + uid + "'";
		}
		
		public void filterColor(int index) {
			curColor = index;
			refresh();
		}
		
		public void filterAgent(ManagerAgent agent) {
			curAgent = agent.id;
			refresh();
		}
		
		@Override public int getCount() { return data.size(); }
		@Override public Object getItem(int arg0) { return data.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }
		
		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			if(view == null)
				view = View.inflate(AgentMemo.this, R.layout.agent_memo_row, null);
			
			AgentManagerMemo item = (AgentManagerMemo) getItem(arg0);
			int color = item.dogColor | 0xFF000000;
			
			ImageView iv = (ImageView)view.findViewById(R.id.ivStatus);
			if(item.isUnreaded()) {
				iv.setImageDrawable(null);
			} else {
				iv.setImageResource(item.isAllowed() ? R.drawable.accept_memo : R.drawable.reject_memo);
			}
			
			int tface = item.isUnreaded() ? Typeface.BOLD : Typeface.NORMAL;
			TextView tv;
			ManagerAgent a = agents.get(item.userid);
			tv = (TextView)view.findViewById(R.id.tvAgent);
			tv.setText(a == null ? item.userid : a.name);
			tv.setTextColor(color);

			String topic = getTopic(item.topic);
			
			String text = item.orgName + "/" + topic  + "/" + item.dogName;
			tv = (TextView)view.findViewById(R.id.tvOrg);
			tv.setText(text);
			tv.setTextColor(color);
			tv.setTypeface(null, tface);
			
			view.setBackgroundResource(arg0 % 2 != 0 ? R.drawable.list_selector : R.drawable.even_row_selector);
			return view;
		}
		
	}

	@Override
	public void selected(ManagerAgent agent) {
		adapter.filterAgent(agent);
	}
}
