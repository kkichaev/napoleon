/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * kki   18/04/2010   creating
 */
package com.grsoft.util.gps;
import com.grsoft.aceteam.R;

import android.content.Context;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.GlobalServiceContext;
import com.grsoft.util.GpsCoord;

/***
 * Провайдер для работы с GPS
 * @author kki
 *
 */
public class GPSUtilOld 
	implements LocationListener, GpsStatus.Listener{
	private static GPSUtilOld instance;
	
	private GPSUtilListenerOld listener;
	
	private LocationManager locationManager;
	private long lastLocationTime;
	private Location lastKnownLoaction;
	
	/***
	 * Время которое будет считаться действительными
	 * последние сняты координаты
	 */
	private final int TIME_FOR_GPS_VALID = Consts.ONE_SECOND * Consts.SEC_PER_MIN * 5; //5 мин
	
	private boolean systemStatusOn;
	
	/***
	 * 
	 * @param appContext
	 */
	private GPSUtilOld(){
		
	}
	
	
	public static GPSUtilOld getInstance(){
		if (instance == null)
			instance = initilize();
		
		return instance;
	}
	
	public static GPSUtilOld initilize(){
		if (instance == null)
			instance = new GPSUtilOld();
		
		instance.init();
		
		return instance;
	}

	public void init()
	{
		stopGPSSystem();
		systemStatusOn = false;
		getTracking();
	}

	@Override
	public void onLocationChanged(Location location) {		
		Log.d("onLocationChanged", "longtitude: " + Integer.toString((int)(location.getLongitude() * Consts.GPS_SCALE)) + 
				"\tlatitude: " + Integer.toString((int)(location.getLatitude() * Consts.GPS_SCALE)));
		
		TrackingOld tracking = getTracking();
		if (tracking != null)
			if (isBetterLocation(location, lastKnownLoaction)){
				tracking.onLocationChanged(location);
				lastKnownLoaction = location;
				lastLocationTime = SystemClock.elapsedRealtime();
			}
	}

	public static boolean isGSMProvider(Location location){
		return location.getProvider().equals(LocationManager.NETWORK_PROVIDER);
	}
	
	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}
	
	private void fireOnStatusChange() {
		if (listener != null){
			listener.onStatusChange(isGpsPosValid());
		}
	}

	public static void start(Context context){
		getInstance();
	}
	
	public void setListener(GPSUtilListenerOld listener){
		this.listener = listener; 
	}
	
	public boolean isGpsPosValid()
	{
		checkGpsListeners();

		if (lastKnownLoaction == null)
			return false;
		
		return (SystemClock.elapsedRealtime() - lastLocationTime) < TIME_FOR_GPS_VALID; 
	}

	@Override
	public void onGpsStatusChanged(int event) {
		fireOnStatusChange();
	}
	
	public boolean updateCurrentLocation()
	{
		boolean result = false;
		
		if (locationManager != null){
			Location location = locationManager.
				getLastKnownLocation(LocationManager.GPS_PROVIDER);
			
			if (location == null)
				location = locationManager.
					getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			
			if (location != null){
				onLocationChanged(location);
				fireOnStatusChange();
			}
			
			result = true;
		}
			
		return result;
	}
	
	private TrackingOld getTracking(){
		TrackingOld result = GpsTrackingManagerOld.getTracking();
		
		if (result != null)
			initGSPSystem();
		else
			stopGPSSystem();
			
		return result;
	}

	private void stopGPSSystem() {
		if (systemStatusOn == true){
			updateLocationListener(false);
			systemStatusOn = false;
		}
	}

	private void initGSPSystem() {
		if (systemStatusOn == false)
			systemStatusOn = updateLocationListener(true);
	}
	
	private boolean updateLocationListener(boolean set){
		boolean result = false;
		locationManager = (LocationManager)
			GlobalServiceContext.service.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
		
		Config config = ConfigManager.getConfig();
		
		if (set == true){
			try {
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
					config.gpsFrequience, config.gpsDistance, this);
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 
					config.gpsFrequience, config.gpsDistance, this);
				result = true;
			} catch(Exception e) {
				locationManager = null;
				e.printStackTrace();
			}
		}
		else{
			locationManager.removeUpdates(this);
			result = true;
		}
		
		return result;
	}
	
	public boolean isGpsEnable(){
		return systemStatusOn;
	}
	
	private static final int TWO_MINUTES = Consts.ONE_SECOND * Consts.SEC_PER_MIN * 2;

	/** Determines whether one Location reading is better than the current Location fix
	  * @param location  The new Location that you want to evaluate
	  * @param currentBestLocation  The current Location fix, to which you want to compare the new one
	  */
	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
	    if (currentBestLocation == null) {
	        // A new location is always better than no location
	        return true;
	    }

	    // Check whether the new location fix is newer or older
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
	    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
	    boolean isNewer = timeDelta > 0;

	    // If it's been more than two minutes since the current location, use the new location
	    // because the user has likely moved
	    if (isSignificantlyNewer) {
	        return true;
	    // If the new location is more than two minutes older, it must be worse
	    } else if (isSignificantlyOlder) {
	        return false;
	    }

	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(location.getProvider(),
	            currentBestLocation.getProvider());

	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}
	
	public GpsCoord getLastKnownLocation() { return isGpsEnable() && isGpsPosValid()  ? 
			new GpsCoord((int)(lastKnownLoaction.getLatitude() * Consts.GPS_SCALE), 
					(int)(lastKnownLoaction.getLongitude() * Consts.GPS_SCALE), lastKnownLoaction.getTime()) : 
						new GpsCoord(0, 0, 0); }
	
	public GpsCoord getLastSavedLocation(){
		return lastKnownLoaction != null ? new GpsCoord((int)(lastKnownLoaction.getLatitude() * Consts.GPS_SCALE), 
				(int)(lastKnownLoaction.getLongitude() * Consts.GPS_SCALE), lastKnownLoaction.getTime()) :
					new GpsCoord(0, 0, 0);
	}
	

	public void stop() {
		stopGPSSystem();
	}
	
	public void checkGpsListeners(){
		if (locationManager == null)
			systemStatusOn = updateLocationListener(true);
	}
	
	public boolean isGpsOK(){
		return locationManager != null && systemStatusOn &&
				(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || 
			    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
	}
}
