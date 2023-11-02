package com.grsoft.util.gps;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.SystemClock;

import com.grsoft.dataobjects.GPSPos;
import com.grsoft.dataobjects.GPSPosImpl;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;

public class GPSUtilNew {
	private static Location lastKnownLoaction;
	public static TheLocationListener locationListener = new TheLocationListener();
	private static TheGpsStatusListener statusListener = new TheGpsStatusListener();
	private static boolean isGpsUpdateInProgress = false;
	public static final int VALID_LOC_TIME = Consts.ONE_SECOND * Consts.SEC_PER_MIN * 2;
	private static Map<String, Boolean> providerAvailable  = new HashMap<String, Boolean>();
	public static int satellitesCount = 0;
	
	public static Object lookLocation = new Object();
	public static Object lookProvider = new Object();
	
	static{
		providerAvailable.put(LocationManager.GPS_PROVIDER, false);
		providerAvailable.put(LocationManager.NETWORK_PROVIDER, false);
	}
	
	/***
	 * јктивность GPS приемника
	 */
	public static boolean isGpsAvailable(){
		synchronized (lookProvider) {
			return providerAvailable.get(LocationManager.GPS_PROVIDER) || 
					providerAvailable.get(LocationManager.NETWORK_PROVIDER);
		}
	}
	
	public static boolean isGpsSystemOn(Context context){
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
				locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}
	 
	/***
	 * ¬рем€ которое будет считатьс€ действительными
	 * последние сн€ты координаты
	 */
	private final static int TIME_FOR_GPS_VALID = Consts.ONE_SECOND * Consts.SEC_PER_MIN * 5; //5 мин

	
	public static class TheLocationListener implements LocationListener{
		@Override
		public void onLocationChanged(Location location) {
			synchronized (lookLocation) {
				if (isBetterLocation(location, lastKnownLoaction)){
					GPSPosImpl gpsPos = new GPSPosImpl();
					gpsPos.setCloseAfterWrite(true);
					GPSPos route = gpsPos.getData();
					route.latitude = (int)(location.getLatitude() * Consts.GPS_SCALE);
					route.longitude = (int)(location.getLongitude() * Consts.GPS_SCALE);
					route.speed = (int)(location.getSpeed() * 100);
					route.date = Util.getDateTime();
					route.isGSM = location.getProvider().equals(LocationManager.NETWORK_PROVIDER) ? 1 : 0;
					route.accuracy = (int)location.getAccuracy();
					route.satellite = satellitesCount;
					gpsPos.write();
					
					/**
					 * помним последнюю удачно прин€тую локацию
					 */
					lastKnownLoaction = location;
				}
			}
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			synchronized (lookProvider) {
				if(providerAvailable.containsKey(provider))
					providerAvailable.put(provider, status == LocationProvider.AVAILABLE);
			}
		}

		@Override
		public void onProviderEnabled(String provider) {}

		@Override
		public void onProviderDisabled(String provider) {}
		
	}
	
	private static class TheGpsStatusListener implements GpsStatus.NmeaListener{

		@Override
		public void onNmeaReceived(long arg0, String arg1) {
			if(arg1.contains("GPGGA")){
				String s[] = arg1.split(",");
				if(s.length > 8){
					try{
						int fix = Integer.parseInt(s[6]);
						if(fix != 0)
							satellitesCount = Integer.parseInt(s[7]);
					}catch(Exception e){
						
					}
				}
			}
			
		}
		
	}

	public static void start(Context context){
		if(!isGpsUpdateInProgress){
			LocationManager locationManager = (LocationManager)context
					.getSystemService(Context.LOCATION_SERVICE);
			
			Config cfg = ConfigManager.getConfig();
			try {
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
					cfg.gpsFrequience, cfg.gpsDistance, locationListener);
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 
					cfg.gpsFrequience, cfg.gpsDistance, locationListener);
				locationManager.addNmeaListener(statusListener);
				isGpsUpdateInProgress = true;
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void stop(Context context){
		if(isGpsUpdateInProgress){
			LocationManager locationManager = (LocationManager)context
					.getSystemService(Context.LOCATION_SERVICE);
			locationManager.removeUpdates(locationListener);
			locationManager.removeNmeaListener(statusListener);
			isGpsUpdateInProgress = false;
		}
	}
	
	public static boolean isGpsPosValid(){
		synchronized (lookLocation) {
			if (lastKnownLoaction == null)
				return false;
			
			return (SystemClock.elapsedRealtime() - lastKnownLoaction.getTime()) < TIME_FOR_GPS_VALID;
		}
	}
	
	
	public static boolean isGpsEnable(Context context){
		boolean result = false;
		
		if(isGpsUpdateInProgress){
			LocationManager manager = (LocationManager)context.
					getSystemService(Context.LOCATION_SERVICE );
			result =  manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
					manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);	
		}
		
		return result;
	}
	
	/** Determines whether one Location reading is better than the current Location fix
	  * @param location  The new Location that you want to evaluate
	  * @param currentBestLocation  The current Location fix, to which you want to compare the new one
	  */
	private static boolean isBetterLocation(Location location, Location currentBestLocation) {
	    if (currentBestLocation == null) {
	        // A new location is always better than no location
	        return true;
	    }

	    // Check whether the new location fix is newer or older
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > VALID_LOC_TIME;
	    boolean isSignificantlyOlder = timeDelta < -VALID_LOC_TIME;
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
	private static boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}
	
	public static GpsCoord getLastKnownLocation(){
		synchronized (lookLocation) {
			return lastKnownLoaction == null ? new GpsCoord(0, 0) : 
				new GpsCoord((int)(lastKnownLoaction.getLatitude() * Consts.GPS_SCALE), 
					(int)(lastKnownLoaction.getLongitude() * Consts.GPS_SCALE)) ;
		}
	}
}
