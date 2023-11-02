package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.grsoft.database.AgentRouteHitching;
import com.grsoft.dataobjects.AgentRoute;
import com.grsoft.dataobjects.AgentRouteItem;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.DataTraveler.Travel;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.AgentRouteImpl;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.LoginData;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.ObjectExportListener;
import com.grsoft.network.RWServiceFactory;
import com.grsoft.network.SendProgressManager;
import com.grsoft.network.UpdateProcessInfo.UpdateStatus;
import com.grsoft.network.UserInfo;
import com.grsoft.network.WriteService;
import com.grsoft.view.TimerMessageBox;

public class AgentRouteEdit extends FragmentActivity{
	private ListView list;
	private AgentRouteImpl agentRoute = new AgentRouteImpl();
	private DayClickListener dayClickListener = new DayClickListener();
	private LongDayClickListener longDayClickListener = new LongDayClickListener();
	private Adapter adapter;
	private ImageButton btnSend;
	
	private Map<String, Map<Integer, Integer>> route = new HashMap<String, Map<Integer, Integer>>();
	private Map<Integer, Integer> maxValMap = new HashMap<Integer, Integer>();
	private String days[] = new String[]{"Ïí", "Âò", "Ñð", "×ò", "Ïò", "Ñá", "Âñ"};
	
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
		
		for(int i = 1; i <= 7; i++)
			maxValMap.put(i, 0);
		
		adapter = new Adapter();
		
		for(Org o : adapter.data){
			Map<Integer, Integer> dp = new HashMap<Integer, Integer>();
			route.put(o.id, dp);
			
			for(int i = 1; i <= 7; i++)
				dp.put(i, 0);
		}
		
		agentRoute.read();
		agentRoute.close();
		
		for(AgentRouteItem i : agentRoute.getData().items){
			if(route.containsKey(i.id)){
				Map<Integer, Integer> dp = route.get(i.id);
				if(dp.containsKey(i.day))
					dp.put(i.day, i.pos);
				
				if(maxValMap.containsKey(i.day)){
					int mv = maxValMap.get(i.day);
					
					if(i.pos > mv)
						maxValMap.put(i.day, i.pos);
				}
			}
		}
		
		list.setAdapter(adapter);
		btnSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(isRouteEmpty())
					Toast.makeText(v.getContext(), R.string.empty_route_cant_send, Toast.LENGTH_SHORT).show();
				else
					send();
			}
		});
	}
	
	private boolean isRouteEmpty(){
		boolean result = true;
		
		for(Entry<String, Map<Integer, Integer>> e : route.entrySet())
			for(Entry<Integer, Integer> v : e.getValue().entrySet())
				if(v.getValue() > 0){
					result = false;
					break;
				}
				
		return result;
	}
	
	private void save(){
		AgentRoute r = agentRoute.getData();
		r.items.clear();
		
		for(Entry<String, Map<Integer, Integer>> e : route.entrySet()){
			
			for(Entry<Integer, Integer> v : e.getValue().entrySet()){
				if(v.getValue() > 0){
					AgentRouteItem iv = new AgentRouteItem();
					iv.day = v.getKey();
					iv.pos = v.getValue();
					iv.id = e.getKey();
					
					r.items.add(iv);
				}
			}
		}
		
		agentRoute.write();
		agentRoute.close();
	}

	protected void send() {
		save();
		new RouteSender().execute((Void[])null);
	}
	
	@Override
	public void onBackPressed() {
		save();
		super.onBackPressed();
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
				Config config = ConfigManager.getConfig();
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

	class LongDayClickListener implements OnLongClickListener{

		@Override
		public boolean onLongClick(View v) {
			String id = (String) ((View)v.getParent().getParent()).getTag();
			int day = getDay(v.getId());
			Bundle b = new Bundle();
			b.putString("id", id);
			b.putInt("day", day);
			
			DialogFragment d = new SelectPosDlg();
			d.setArguments(b);
			d.show(getSupportFragmentManager(), d.getClass().getCanonicalName());
			
			return true;
		}
		
	}
	
	class SelectPosDlg extends DialogFragment{
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(AgentRouteEdit.this);
			
			String[] items = new String[adapter.getCount()];
			
			for(int i = 1; i <= adapter.getCount(); i++)
				items[i-1] = Integer.toString(i);
			
			Bundle b = getArguments();
			
			if(b != null){
				final String id = b.getString("id");
				final Integer day = b.getInt("day");
				
				int ch = 0;
				
				if(route.containsKey(id) && route.get(id).containsKey(day))
					ch = route.get(id).get(day);
				
				if(ch == 0 && maxValMap.containsKey(day))
					ch = maxValMap.get(day);
					
				if(ch > 0)
					ch--;
				
				builder.setSingleChoiceItems(items, ch, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(route.containsKey(id) && route.get(id).containsKey(day)){
							int pos = which + 1;
							route.get(id).put(day, pos);
							
							if(maxValMap.containsKey(day)){
								int p = maxValMap.get(day);
								
								if (pos > p)
									maxValMap.put(day, pos);
							}
							
							adapter.notifyDataSetChanged();
							dismiss();
						}
					}
				});
			}
			
			return builder.create();
		}
	}
	
	class DayClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			String id = (String) ((View)v.getParent().getParent()).getTag();
			int day = getDay(v.getId());
			
			if(route.containsKey(id)){
				Map<Integer, Integer> dp = route.get(id);
				
				if(dp.containsKey(day)){
					int d = dp.get(day);
					
					if(d > 0){
						dp.put(day, 0);

						if(maxValMap.containsKey(day)){
							int p = maxValMap.get(day);
							
							if(p == d)
								maxValMap.put(day, --p);
						}
					}else
						dp.put(day, maxVal(day));
				}
			}

			adapter.notifyDataSetChanged();
		}
	}
	
	private int getDay(int id) {
		int result = 0;
		
		if (id == R.id.tvMon)
			result = 1;
		else if (id == R.id.tvTue)
			result = 2;
		else if (id == R.id.tvWed)
			result = 3;
		else if (id == R.id.tvThu)
			result = 4;
		else if (id == R.id.tvFri)
			result = 5;
		else if (id == R.id.tvSat)
			result = 6;
		else if (id == R.id.tvSun)
			result = 7;
		
		return result;
	}
	
	class Adapter extends BaseAdapter {
		public List<Org> data = new ArrayList<Org>();

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
			
			TextView tv = (TextView) view.findViewById(R.id.tvName);
			String str = "<b>" + org.name + "</b><br>" + org.address;
			tv.setText(Html.fromHtml(str));
			
			String id = org.id;
			
			tv = (TextView) view.findViewById(R.id.tvMon);
			updateView(id, tv, 1);
			
			tv = (TextView) view.findViewById(R.id.tvTue);
			updateView(id, tv, 2);
			
			tv = (TextView) view.findViewById(R.id.tvWed);
			updateView(id, tv, 3);
			
			tv = (TextView) view.findViewById(R.id.tvThu);
			updateView(id, tv, 4);
			
			tv = (TextView) view.findViewById(R.id.tvFri);
			updateView(id, tv, 5);
			
			tv = (TextView) view.findViewById(R.id.tvSat);
			updateView(id, tv, 6);
			
			tv = (TextView) view.findViewById(R.id.tvSun);
			updateView(id, tv, 7);
			
			return view;
		}

		public void updateView(String id, TextView tv, int day) {
			tv.setOnClickListener(dayClickListener);
			tv.setOnLongClickListener(longDayClickListener);
			String text = createText(id, day);
			tv.setText(text);
			
			if(text.length() > 2){
				tv.setBackgroundColor(getResources().getColor(R.color.grey));
				tv.setTextColor(getResources().getColor(R.color.red));
			}else{
				tv.setBackgroundColor(getResources().getColor(R.color.white));
				tv.setTextColor(getResources().getColor(R.color.blue));
			}
		}
	}

	public Integer maxVal(int day) {
		int result = 0;
		
		if(maxValMap.containsKey(day)){
			result = maxValMap.get(day);
			maxValMap.put(day, ++result);
		}
		
		return result;
	}

	public String createText(String id, int day) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(days[day-1]);
		int pos = 0;
		
		if(route.containsKey(id)){
			Map<Integer, Integer> dp = route.get(id);
			
			if(dp.containsKey(day)){
				pos = dp.get(day);
				
				if(pos > 0)
					sb.append(" : ").append(String.format("%02d", pos));
			}
		}
		
		return sb.toString();
	}
}
