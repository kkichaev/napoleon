package com.grsoft.napoleon.dostavka;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.grsoft.dataobjects.RouteItemRow;
import com.grsoft.dataobjects.RoutePoint;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.RoutePointImpl;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.dostavka.MainService.LocalBinder;
import com.grsoft.network.SyncProgress;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Updater;
import com.grsoft.util.Util;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class Main extends FragmentActivity{
	private static final String FILTER_KEY = "filter";
	protected MainService mainsrv;
	boolean bound = false;
	protected SyncProgress progress = new SyncProgress();
	protected Date workDate = new Date();
	View calendarView;
	private Menu menu;
	
	protected ListView list;
	
	public static Class<? extends MainAdapter> ADAPTER_CLASS = MainAdapter.class;
	protected MainAdapter adapter;
	
//	@Override 
	protected int getLayoutID() { return R.layout.main;	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutID());
	
		list = (ListView) findViewById(R.id.list);

		try {
			@SuppressWarnings("rawtypes")
			Class[] args = new Class[] {
					Context.class,
					Date.class,
					View.OnClickListener.class,
					boolean.class,
			};
			adapter =  ADAPTER_CLASS.getDeclaredConstructor(args).newInstance(this, workDate, mapClick, false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				RouteItemRow rp = (RouteItemRow) parent.getItemAtPosition(position);
				RoutePointView.open(Main.this, rp.item);
			}});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(refresh, new IntentFilter(MainService.SYNC_FINISHED));
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(refresh);
	}
	
	BroadcastReceiver refresh = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getBooleanExtra(MainService.SYNC_RESULT, false))
				reload();
		}
	};

	protected void reload() {((MainAdapter)adapter).reload(workDate, false);}
	
	protected int getOptionsMenuID() { return R.menu.main_option_menu; }
		
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(getOptionsMenuID(), menu);

		final MenuItem i = menu.findItem(R.id.itCalendar);
    	if(i != null)
    		i.getActionView().setOnClickListener(new OnClickListener() {
				@Override public void onClick(View v) { menu.performIdentifierAction(i.getItemId(), 0);	} });
    	
    	this.menu = menu;
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem itExit = menu.findItem(R.id.itExit);
		
		if(itExit != null)
			itExit.setEnabled(RoutePointImpl.isRouteComplete());
		
		MenuItem i = menu.findItem(R.id.itCalendar);
    	if(i != null){
    		calendarView = i.getActionView();
    		refreshCalendarDate();
    	}		
		return true;
		
	}
	
	void refreshCalendarDate() {
		if(calendarView != null){
			SimpleDateFormat dayFmt = new SimpleDateFormat("dd");
			TextView tv = (TextView) calendarView.findViewById(R.id.tvCurDate);
			tv.setText(dayFmt.format(workDate));
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId(); 
		if (id == R.id.itFilter) {
			applayFilter();
			return true;
		}else if( id == R.id.itSync){
			doSync(false);
			return true;
		}else if(id == R.id.itCall){
			callToOfiice();
			return true;
		}else if(id == R.id.itSetting){
			showSetting();
			return true;
		}else if(id == R.id.itAbout){
			showAbout();
			return true;
		}else if(id == R.id.itCalendar){
			RouteCalendar.open(this);
			return true;
		}else if(id == R.id.itExit){
			exit();
			return true;
		}
			
		return super.onOptionsItemSelected(item);
	}

	private void applayFilter() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		boolean f = !pref.getBoolean(FILTER_KEY, false);
		Editor ed = pref.edit();
		ed.putBoolean(FILTER_KEY, f);
		ed.commit();
		
		menu.findItem(R.id.itFilter).setIcon(f ? R.drawable.ic_menu_filter_off : R.drawable.ic_menu_filter);
		adapter.reload(workDate, f);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent data) {
		if(arg0 == RouteCalendar.CALENDAR_REQ && arg1 == RESULT_OK && data != null) {
			Date curDate = new Date();
			long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
			workDate = new Date(ct);
			refreshCalendarDate();
			reload();
		}
	}
	
	private void showAbout() { showDialog(R.id.about_dlg); }

	private void exit() {
		if(allowExit()){
			if(bound)
				mainsrv.stopSelf();
			
			finish();
		}
	}

	private boolean allowExit() { return true; }

	private void showSetting() {
		Setting.open(this);
	}

	private void callToOfiice() {
		StringBuilder sb = new StringBuilder();
		ConfigImpl cfg = new ConfigImpl();
		if(cfg.getValue(sb, "OfficeNumber"))
			phoneCall(this, sb.toString());
	}
	
	public static void phoneCall(Context context, String number){
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
		context.startActivity(intent);
	}
	
	protected void onServiceConnected() {}

	private ServiceConnection srvcon = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			 LocalBinder binder = (LocalBinder) service;
			 mainsrv = binder.getService();
	         bound = true;
	         Main.this.onServiceConnected();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			bound = false;
		}
    };
    
	@Override
	protected void onStart() {
		super.onStart();
		Intent intent = new Intent(this, MainService.class);
		bindService(intent, srvcon, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		unbindBoundedService();
	}

	protected void unbindBoundedService() {
		if (bound) {
            unbindService(srvcon);
            bound = false;
	     }
	}
	
	public void doSync(boolean clear) {
		if(bound){
			progress.show(getSupportFragmentManager(), progress.getClass().toString());
			mainsrv.sync(clear);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == R.id.about_dlg){
			return createAboutDlg();
		}else
			return super.onCreateDialog(id);
	}

	@SuppressLint("InflateParams")
	private Dialog createAboutDlg() {
		View messageView = getLayoutInflater().inflate(R.layout.about, null, false);
        TextView tvLink = (TextView) messageView.findViewById(R.id.tvLink);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(messageView);
        
        if(Features.LINKS_DISSALLOW){
	        tvLink.setEnabled(false);
	        tvLink.setMovementMethod(null);
        }
        
        tvLink.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { dismissDialog(R.id.about_dlg);} });
        
        messageView.findViewById(R.id.btnCheckUpdates).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				new Updater(){
					protected void onPreExecute() {
						Toast.makeText(v.getContext(), R.string.check_updating,
								Toast.LENGTH_SHORT).show();
					};
					
					protected void onPostExecute(Boolean result) {
						if(!result)
							Toast.makeText(v.getContext(), R.string.update_not_found,
									Toast.LENGTH_SHORT).show();
					};
					
				}.execute(v.getContext());
			}
    	});

        return builder.create();
	}
	
	OnClickListener mapClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			RouteItemRow ri = (RouteItemRow) v.getTag();
			if(ri != null){
				RoutePointImpl org = new RoutePointImpl();
				
				if(org.read("id", ri.item.id)){
					RoutePoint rp = org.getData();
					String address = rp.address;
					String s = String.format("geo:0,0?q=%s", address );
					
					if(rp.latitude != 0 && rp.longitude != 0)
						s = String.format("geo:%s,%s", Util.IntToScaleStr(rp.latitude, Consts.GPS_SCALE), Util.IntToScaleStr(rp.longitude, Consts.GPS_SCALE));
					
					try{
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
						startActivity(intent);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}
	};
}
