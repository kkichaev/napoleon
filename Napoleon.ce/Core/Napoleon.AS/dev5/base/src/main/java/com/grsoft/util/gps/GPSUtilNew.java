package com.grsoft.util.gps;
import com.grsoft.aceteam.R;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.grsoft.dataobjects.GPSPos;
import com.grsoft.dataobjects.GPSPosImpl;
import com.grsoft.dataobjects.impl.LogImpl;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;
import android.content.Context;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.location.OnNmeaMessageListener;
import android.os.Bundle;

public class GPSUtilNew {
	private static Location lastKnownLocation;
	private static Date lastRcvLocationTime;
	public static TheLocationListener locationListener = new TheLocationListener();
	//private static TheGpsStatusListener statusListener = new TheGpsStatusListener();
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
	 * Активность GPS приемника
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
	 * Время которое будет считаться действительными
	 * последние сняты координаты
	 */
	public static int TIME_FOR_GPS_VALID = Consts.ONE_SECOND * Consts.SEC_PER_MIN * 10; //10 мин

	
	public static class TheLocationListener implements LocationListener{
		@Override
		public void onLocationChanged(Location location) { putLocation(location); }

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

//	private static class TheGpsStatusListener implements OnNmeaMessageListener {
//		private String GPRMC = "V";
//		private Date lastGPRMC = null;
//
//		@Override
//		public void onNmeaMessage(String nmea, long arg0) {
//			String s[] = nmea.split(",");
//
//			if(nmea.contains("GPRMC") && s.length > 3){
//				Date now = new Date();
//				//Задержка дребезга для связи со спутниками
//				if(lastGPRMC == null || Math.abs(lastGPRMC.getTime() - now.getTime()) > TIME_FOR_GPS_VALID){
//					String v = s[2];
//					lastGPRMC = now;
//
//					if(!GPRMC.equals(v)){
//						if(v.equals("A"))
//							LogImpl.log(com.grsoft.dataobjects.Log.PDA_STATUS, com.grsoft.dataobjects.Log.MANAGER, "Установлена связь со спутниками.");
//						else{
//							LogImpl.log(com.grsoft.dataobjects.Log.PDA_STATUS, com.grsoft.dataobjects.Log.MANAGER, "Потеряна связь со спутниками.");
//							satellitesCount = 0;
//						}
//						GPRMC = v;
//					}
//				}
//			}else if(nmea.contains("GPGGA") && s.length > 8){
//				try{
//					int fix = Integer.parseInt(s[6]);
//
//					if(fix > 0)
//						satellitesCount = Integer.parseInt(s[7]);
//					else
//						satellitesCount = 0;
//				}catch(Exception e){}
//			}
//		}
//	}

//	private static class TheGpsStatusListener implements GpsStatus.NmeaListener{
//		private String GPRMC = "V";
//		private Date lastGPRMC = null;
//
//		@Override
//		public void onNmeaReceived(long arg0, String nmea) {
//			String s[] = nmea.split(",");
//
//			if(nmea.contains("GPRMC") && s.length > 3){
//				Date now = new Date();
//				//Задержка дребезга для связи со спутниками
//				if(lastGPRMC == null || Math.abs(lastGPRMC.getTime() - now.getTime()) > TIME_FOR_GPS_VALID){
//					String v = s[2];
//					lastGPRMC = now;
//
//					if(!GPRMC.equals(v)){
//						if(v.equals("A"))
//							LogImpl.log(com.grsoft.dataobjects.Log.PDA_STATUS, com.grsoft.dataobjects.Log.MANAGER, "Установлена связь со спутниками.");
//						else{
//							LogImpl.log(com.grsoft.dataobjects.Log.PDA_STATUS, com.grsoft.dataobjects.Log.MANAGER, "Потеряна связь со спутниками.");
//							satellitesCount = 0;
//						}
//						GPRMC = v;
//					}
//				}
//			}else if(nmea.contains("GPGGA") && s.length > 8){
//				try{
//					int fix = Integer.parseInt(s[6]);
//
//					if(fix > 0)
//						satellitesCount = Integer.parseInt(s[7]);
//					else
//						satellitesCount = 0;
//				}catch(Exception e){}
//			}
//		}
//	}

	public static void start(Context context){
		Config cfg = ConfigManager.getConfig();
		start(context, cfg.gpsFrequience, cfg.gpsDistance);
	}
	
	public static void start(Context context, int freq, int dist){
		if(isGpsUpdateInProgress)
			stop(context);
		
		LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		
		try {
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,  freq, dist, locationListener);
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, freq, dist, locationListener);
			//locationManager.addNmeaListener(statusListener);
			isGpsUpdateInProgress = true;
		} catch(Exception e) {
			LogImpl.log(com.grsoft.dataobjects.Log.EXCEPTION, com.grsoft.dataobjects.Log.MANAGER, e.getMessage());
		}
	}
	
	public static void stop(Context context){
		if(isGpsUpdateInProgress){
			LocationManager locationManager = (LocationManager)context
					.getSystemService(Context.LOCATION_SERVICE);
			locationManager.removeUpdates(locationListener);
			//locationManager.removeNmeaListener(statusListener);
			isGpsUpdateInProgress = false;
		}
	}
	
	public static boolean isGpsPosValid(){
		return isGpsPosValid(TIME_FOR_GPS_VALID);
	}

	public static boolean isGpsPosValid(int validTime){
		synchronized (lookLocation) {
			if (lastRcvLocationTime == null)
				return false;
			
			boolean result = (new Date().getTime() - lastRcvLocationTime.getTime()) < validTime;
			
			if(!result)
				lastKnownLocation = null;
			
			return result;
		}
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
	
	public static Location getCurrentLocation(Context context) {
		try {
			if (context != null) {
				synchronized (lookLocation) {
					LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
					Location l = (locationManager == null) ? null : locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
					if (l != null) {
						if (lastKnownLocation == null || l.getTime() > lastKnownLocation.getTime())
							putLocation(l);
					}
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		} finally {
			return lastKnownLocation;
		}
	}
	
	public static GpsCoord getLastKnownLocation(Context context){
		return new GpsCoord(getCurrentLocation(context));
	}

	public static GpsCoord getLastKnownLocation(){
		return getLastKnownLocation(null);
	}

	static void putLocation(Location location) {
		synchronized (lookLocation) {
			if ((location.getLatitude() != 0.0 || location.getLongitude() != 0.0) &&
					isBetterLocation(location, lastKnownLocation)){
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
				route.stltime = new Date(location.getTime());
				
				if (android.os.Build.VERSION.SDK_INT >= 18){
					try{
						Method m = location.getClass().getMethod("isFromMockProvider", (Class[])null);
						route.isMock = (Boolean)m.invoke(location, (Object[])null) ? 1 : 0;
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				
				gpsPos.write();
				
				/**
				 * помним последнюю удачно принятую локацию
				 */
				lastKnownLocation = location;
				lastRcvLocationTime = new Date();
			}
		}
	}
}
