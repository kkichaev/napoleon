package com.grsoft.napoleon.dostavka;

import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.RoutePointImpl;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.dostavka.MainService.LocalBinder;
import com.grsoft.network.BaseFragmentActivity;
import com.grsoft.network.SyncProgress;
import com.grsoft.util.Updater;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends BaseFragmentActivity{
	private MainService mainsrv;
	boolean bound = false;
	private SyncProgress progress = new SyncProgress();
	
	@Override protected int getLayoutID() { return R.layout.main;	}

	@Override
	protected void init() {
		super.init();
		
		openRouteFragment();
		
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
		String activepoint = p.getString(PointFragment.POINTID, "");
		if(activepoint.trim().length() > 0)
			openPointFragment(activepoint);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_option_menu, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem itExit = menu.findItem(R.id.itExit);
		
		if(itExit != null)
			itExit.setEnabled(RoutePointImpl.isRouteComplete());
		
		return true;
		
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId(); 
		if( id == R.id.itSync){
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
		}else if(id == R.id.itExit){
			exit();
			return true;
		}
			
		return super.onOptionsItemSelected(item);
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
		Intent i = new Intent(this, Setting.class);
		startActivity(i);
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

	private ServiceConnection srvcon = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			 LocalBinder binder = (LocalBinder) service;
			 mainsrv = binder.getService();
	         bound = true;
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
        bindService(intent, srvcon, Context.BIND_ADJUST_WITH_ACTIVITY);
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

	public void openPointFragment(String id) {
		Bundle args = new Bundle();
		args.putString(PointFragment.POINTID, id);
		Fragment frg = new PointFragment();
		frg.setArguments(args);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.fragment_container, frg);
		ft.addToBackStack(frg.getClass().getCanonicalName());
		ft.commit();
	}

	public void openRouteFragment() {
		getFragmentManager().popBackStack();
		Fragment frg = new RouteFragment();;
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.fragment_container, frg);
		ft.commit();
	}
	
	protected void saveActivePoint(String id) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		Editor ed = sp.edit();
		ed.putString(PointFragment.POINTID, id);
		ed.commit();
	}
	
	@Override
	public void onBackPressed() {
		int count = getFragmentManager().getBackStackEntryCount();
	    if (count > 0) {
	    	Fragment f = getFragmentManager().findFragmentById(R.id.fragment_container);
	    	
	    	if (f instanceof PointFragment && ((PointFragment)f).isAllowClose()){
	    		saveActivePoint("");
	    		getFragmentManager().popBackStack();
	    	}
	    }
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == R.id.about_dlg){
			return createAboutDlg();
		}else
			return super.onCreateDialog(id);
	}

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
}
