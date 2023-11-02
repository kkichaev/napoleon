package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import com.grsoft.database.AgentRouteHitching;
import com.grsoft.dataobjects.AgentRoute;
import com.grsoft.dataobjects.AgentRouteItem;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.DataTraveler.Travel;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.AgentRouteImpl;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.LoginData;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.ObjectExportListener;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.SendProgressManager;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.network.UserInfo;
import com.grsoft.network.WriteService;
import com.grsoft.util.Util;
import com.grsoft.view.TimerMessageBox;

public class AgentRouteEdit extends Activity {
	private ListView list;
	private AgentRouteImpl agentRoute = new AgentRouteImpl();
	private DayClickListener dayClickListener = new DayClickListener();
	private Adapter adapter;
	private ImageButton btnSend;
	
	public static void open(Context context) {
		Intent intent = new Intent(context, AgentRouteEdit.class);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.agentrouteedit);

		list = (ListView) findViewById(R.id.list);
		btnSend = (ImageButton) findViewById(R.id.btnSend);
		
		adapter = new Adapter();
		list.setAdapter(adapter);
		
		agentRoute.read();
		agentRoute.close();
		
		btnSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(agentRoute.getData().items.size() == 0)
					showDialog(R.id.empty_route_cant_send);
				else
					send();
			}
		});
	}

	protected void send() {
		new RouteSender().execute((Void[])null);
	}
	
	class RouteSender extends NetworkAsyncTask{
		private int traffic = 0;
		
		public RouteSender() {
			super(new SendProgressManager(AgentRouteEdit.this, btnSend));
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			AgentRouteHitching hitching = new AgentRouteHitching();
			
			if (hitching.size() == 0)
				return true;
			
			onUpdate(UpdateStatus.START_OF_PROCESS, 0);

			try	{
				CfgNpl config = (CfgNpl) ConfigManager.getConfig();
				UserInfo userInfo = new LoginData(config.login, config.passw, config.impersonate, AgentRouteEdit.this);
				List<ObjectExportListener> export = new ArrayList<ObjectExportListener>();
				export.add(hitching);
				WriteService writeService = (WriteService) RWServiceFactory
						.instance.createWriteService(export);
				writeService.setUpdateProcessListenet(this);
				
				if (!writeService.write(AgentRouteEdit.this, userInfo)){
					onUpdate(UpdateStatus.END_OF_PROCESS, 0);
					showErrorMsg(writeService.getMessage(), AgentRouteEdit.this);
					
					return false;
				}
				else{
					traffic += writeService.getSendedBytes();
					onUpdate(UpdateStatus.END_OF_PROCESS, 0);
					onUpdateMessage(new TimerMessageBox(getString(R.string.inform), 
						getString(R.string.sync_end_traffic) + 
						Integer.toString((traffic + 512) / 1024) + getString(R.string.kB), 
						AgentRouteEdit.this));
					
					return true;
				}
			} catch(Exception exception){
				onUpdate(UpdateStatus.END_OF_PROCESS, 0);
				showErrorMsg(exception.getMessage(), AgentRouteEdit.this);
				exception.printStackTrace();
				
				return false;
			} 
		}
		
		@Override
		protected void onPreExecute()
		{
			btnSend.setEnabled(true);
		}
	}

	class DayClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			String id = (String) ((View)v.getParent().getParent()).getTag();
			AgentRoute a = agentRoute.getData();
			a.date = Util.getDateTime();
			a.params &= ~ParamState.ofExported;
			
			boolean set = false;
			
			for(AgentRouteItem item : a.items){
				if(item.id.equals(id)){
					item.day = setDay(item.day, v.getId());
					if(item.day == 0)
						a.items.remove(item);
					
					set = true;
					break;
				}
			}
			
			if(!set){
				AgentRouteItem item = new AgentRouteItem();
				item.id = id;
				item.day = setDay(0, v.getId());
				a.items.add(item);
			}
			
			agentRoute.write();
			agentRoute.close();
			adapter.notifyDataSetChanged();
		}

		private int setDay(int day, int id) {
			switch(id){
			case R.id.tvMon:
				day ^= 1;
				break;
			case R.id.tvTue:
				day ^= 2;
				break;
			case R.id.tvWed:
				day ^= 4;
				break;
			case R.id.tvThu:
				day ^= 8;
				break;
			case R.id.tvFri:
				day ^= 16;
				break;
			case R.id.tvSat:
				day ^= 32;
				break;
			case R.id.tvSun:
				day ^= 64;
				break;
			}
			
			return day;
		}
	}
	
	class Adapter extends BaseAdapter {
		List<Org> data = new ArrayList<Org>();

		public Adapter() {
			DataTraveler.travel(Org.class, new Travel<Org>() {
				@Override
				public boolean travel(DataTraveler<Org> item) {
					data.add(item.data);
					item.data = new Org();
					return true;
				}
			}, null);
			
			Collections.sort(data, new Comparator<Org>() {
				@Override
				public int compare(Org lhs, Org rhs) {
					return lhs.name.compareTo(rhs.name);
				}
			});
		}

		@Override
		public int getCount() {	return data.size();	}

		@Override
		public Object getItem(int position) { return data.get(position); }

		@Override
		public long getItemId(int position) { return 0;	}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if(view == null)
				view = View.inflate(AgentRouteEdit.this, R.layout.agentrouterow, null);
			
			Org org = (Org) getItem(position);
			view.setTag(org.id);

			AgentRouteItem item = null;
			
			for(AgentRouteItem i : agentRoute.getData().items)
				if(i.id.equals(org.id))
					item = i;
			
			TextView tv = (TextView) view.findViewById(R.id.tvName);
			String str = "<b>" + org.name + "</b><br>" + org.address;
			tv.setText(Html.fromHtml(str));
			
			tv = (TextView) view.findViewById(R.id.tvMon);
			updateView(item, tv,1);
			
			tv = (TextView) view.findViewById(R.id.tvTue);
			updateView(item, tv, 2);
			
			tv = (TextView) view.findViewById(R.id.tvWed);
			updateView(item, tv, 4);
			
			tv = (TextView) view.findViewById(R.id.tvThu);
			updateView(item, tv, 8);
			
			tv = (TextView) view.findViewById(R.id.tvFri);
			updateView(item, tv, 16);
			
			tv = (TextView) view.findViewById(R.id.tvSat);
			updateView(item, tv, 32);
			
			tv = (TextView) view.findViewById(R.id.tvSun);
			updateView(item, tv, 64);
			
			return view;
		}

		public void updateView(AgentRouteItem item, TextView tv, int mask) {
			tv.setOnClickListener(dayClickListener);
			
			if(item != null && (item.day & mask) == mask){
				tv.setBackgroundColor(getResources().getColor(R.color.grey));
				tv.setTextColor(getResources().getColor(R.color.red));
			}else{
				tv.setBackgroundColor(getResources().getColor(R.color.white));
				tv.setTextColor(getResources().getColor(R.color.blue));
			}
		}
	}
}
