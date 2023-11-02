package com.grsoft.manager;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.grsoft.dataobjects.impl.DivisionManagerImpl;
import com.grsoft.util.gps.GPSUtilNew;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import androidx.core.content.PermissionChecker;
import android.view.View;
import android.widget.TextView;

public class GPSChecker {
	static long WAIT_GPS_TIMEOUT = 30 * 1000; // 30 sec 
	static AlertDialog waitGpsDlg;
	static Runnable runnable;
	static long startWaitTime = 0;
	static long GPS_PROBE_WAIT = 500; // 0.5 sec
	static Timer gpsProbeTimer = null;
	
	static boolean hasLocationPermission(Activity owner) {
		return PermissionChecker.checkSelfPermission(owner, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED;
	}

	static boolean isGPSTurnOn(Activity owner){
		LocationManager locationManager = (LocationManager) owner.getSystemService(Activity.LOCATION_SERVICE);
		return locationManager.isProviderEnabled (LocationManager.GPS_PROVIDER);
	}
	
	static void gpsScan(final Activity owner) {
		GPSUtilNew.stop(owner);
		GPSUtilNew.start(owner);
		
//		waitGpsTimer = new WaitGpsTimer();
//		waitGpsTimer.setHandler(handler);
		AlertDialog.Builder builder = new AlertDialog.Builder(owner);
		builder.setTitle(R.string.please_wait);
		View v = View.inflate(owner,  R.layout.progress_dialog, null);
		TextView tv = (TextView)v.findViewById(R.id.tvInfo);
		tv.setText(R.string.gpsWait);
		
		builder.setView(v);
		
		if(waitGpsDlg != null) {
			try {
				waitGpsDlg.dismiss();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		waitGpsDlg = builder.create();
		waitGpsDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override public void onDismiss(DialogInterface arg0) { GPSChecker.waitDialogClosed(); }
		});			
		waitGpsDlg.show();

		startWaitTime = new Date().getTime();
		
		if(gpsProbeTimer != null) {
			gpsProbeTimer.cancel();
		}
		gpsProbeTimer = new Timer();
		gpsProbeTimer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				if(GPSUtilNew.isGpsPosValid() || (new Date()).getTime() - startWaitTime >= WAIT_GPS_TIMEOUT) {
					if(waitGpsDlg != null) {
						synchronized (waitGpsDlg) {
							waitGpsDlg.dismiss();
							waitGpsDlg = null;
						}
						
						if(runnable != null)
							runnable.run();
					}
					
					gpsProbeTimer.cancel();
					gpsProbeTimer = null;
				}
			}
		}, GPS_PROBE_WAIT, GPS_PROBE_WAIT);
	}
	
	static public void waitDialogClosed() {
		if(waitGpsDlg != null)
			waitGpsDlg = null;
		
		if(gpsProbeTimer != null) {
			gpsProbeTimer.cancel();
			gpsProbeTimer = null;
		}
	}
	
	static void askPermission(final Activity context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.gps_permission_dissallow);
		builder.setMessage(R.string.gps_permission_explain);
		builder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent appSettingsIntent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS",
		           Uri.parse("package:" + context.getPackageName()));
				context.startActivity(appSettingsIntent);
				dialog.dismiss();
			}
		});
		
		builder.create().show();
	}
	
	static void askTurnGPSOn(final Activity activity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(R.string.gpsOffTitle);
		builder.setMessage(R.string.gpsOffMessage);
		builder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				activity.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
	
	public static void check(Activity owner, Runnable runnable) {
		if (DivisionManagerImpl.isMobile()) {
			if(GPSUtilNew.isGpsPosValid() == false) {
				if(!hasLocationPermission(owner)) {
					askPermission(owner);
				} else if(!isGPSTurnOn(owner)) {
					askTurnGPSOn(owner);
				} else {
					gpsScan(owner);
				}
				GPSChecker.runnable = runnable;
				return;
			}
		}
		runnable.run();
	}
}
