package com.grsoft.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.ManagerAgent;
import com.grsoft.dataobjects.UserLocation;
import com.grsoft.manager.MapData.AgentInField;
import com.grsoft.napoleon.util.CfgMgr;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class AgentsInFields extends DrawerActivity implements OnItemClickListener {
	private MapHelper mapHelper = new MapHelper();
	private WebView webView;
	private ListView list;
	private MapTypeSelectorHelper mtSelectHelper = new MapTypeSelectorHelper();
	
	public static void open(Context context){
		Intent i = new Intent(context, AgentsInFields.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) ;
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		webView = (WebView) findViewById(R.id.wv);
		list = (ListView) findViewById(R.id.list);
		
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSupportZoom(true);
		webView.setWebChromeClient(new WebChromeClient());
		
		list.setOnItemClickListener(this);
	}
	
	@Override
	protected int getLayoutID() { return R.layout.agents_in_fields; }
	
	@Override protected void postSyncUpdate() {
		final List<UserLocation> loc = new ArrayList<UserLocation>();
		String where = String.format("date > %d", Util.resetTime(new Date()).getTime()) ;
		//where = null;
		DataTraveler.travel(UserLocation.class, new DataTraveler.Travel<UserLocation>(true){

			@Override
			public boolean travel(DataTraveler<UserLocation> item) {
				loc.add(item.data);
				return true;
			}}, where);
		
		Collections.sort(loc, new Comparator<UserLocation>(){
			@Override
			public int compare(UserLocation lhs, UserLocation rhs) {
				return lhs.stltime.compareTo(rhs.stltime);
			}});
		
		final Map<String, ManagerAgent> agents = new HashMap<String, ManagerAgent>();
		
		DataTraveler.travel(ManagerAgent.class, new DataTraveler.Travel<ManagerAgent>(true){

			@Override
			public boolean travel(DataTraveler<ManagerAgent> item) {
				if(!agents.containsKey(item.data.id))
					agents.put(item.data.id, item.data);
				
				return true;
			}}, null);
		
		
		List<MapData.AgentInField> data = new ArrayList<MapData.AgentInField>();
		
		int idx = 1;
		
		
		for(UserLocation u : loc){
			if (agents.containsKey(u.userid)){
				MapData.AgentInField a = new MapData.AgentInField();
				a.date = u.date;
				a.idx = idx++;
				a.agent = agents.get(u.userid);
				a.latitude = (double) u.latitude / Consts.GPS_SCALE;
				a.longitude = (double) u.longitude / Consts.GPS_SCALE;
				
				data.add(a);
			}
		}
		
		MapData m = new MapData();
		m.agentsinfields.addAll(data);
		
		CfgMgr cfg = (CfgMgr) ConfigManager.getConfig();
		String html = mapHelper.createMap(this, m, cfg.maptype);
		webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
		
		list.setAdapter(new AgentsInFieldsAdapter(this, data));
	}

	@Override protected String getActionBarTitle() { return getString(R.string.users_locations); }
	
	@Override
	protected void initHitchings(List<Hitching> list) {
		list.add(new RcvNewHitching(UserLocation.class));
	}

	@Override
	protected void onResume() {
		super.onResume();
		doSync();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		mtSelectHelper.addMenuItem(menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.itMapType){
			showDialog(R.id.select_map_type_dlg);
			return true;
		}else
			return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == R.id.select_map_type_dlg)
			return mtSelectHelper.createSelectMapTypeDlg(this);
		else
			return super.onCreateDialog(id);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if(id == R.id.select_map_type_dlg)
			mtSelectHelper.prepareSelectMapTypeDlg(dialog);
		else
			super.onPrepareDialog(id, dialog);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		MapData.AgentInField d = (AgentInField) parent.getItemAtPosition(position);
		SyncDetail.sync(this, createUpdateCtrl(this, d.date, d.agent.id), d.agent.id, d.date, true);
	}
	
	private UpdateCtrl createUpdateCtrl(final Activity activity, final Date date, final String userid) {
		return new UpdateCtrl() {
			@Override public void onFinish(boolean result) {
				if( result )
					AgentRouteNew.open(activity, userid, date, AgentRouteNew.MAP_VIEW_TYPE);
			}
			@Override public void updateCtrl(boolean enabled) {} };
	}
	
	@Override protected int getOptionsMenuID() { return R.menu.agents_in_fields_menu;	}
}
