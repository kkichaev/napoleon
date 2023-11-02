package com.grsoft.util.gps;

import java.util.Calendar;

import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.napoleon.FeaturesBase;
import com.grsoft.napoleon.util.ConfigManager;

public class GpsTrackingManagerOld {
	public final static String GPS_CHECK_STATUS_DISABLE = "manage_gps";
	public static TrackingOld tracking = new CoordTrackingOld();
	
	final static String GSM_ID = "GSM";
	final static String GPS_POINT_ID = "GPSpoint";
	public final static String GPS_ROUTE_ID = "GPSroute";
	public final static String KEY_VAL = "Tracking";
	public final static String KEY_GPSPERIOD = "gpstimecond";
		
	public static TrackingOld getTracking(){
		Boolean gpsCheckStatusDisable = 
				(Boolean)ConfigManager.getConfig().getProperty(GPS_CHECK_STATUS_DISABLE);
		TrackingOld result = null;
		
		if (gpsCheckStatusDisable != null && gpsCheckStatusDisable == true)
			result = tracking;
		else {
			ConfigImpl configImpl = new ConfigImpl();
			configImpl.getData().key = KEY_VAL;
			
			try{
				if (configImpl.read()){
					String code = configImpl.getData().value;
					if(!FeaturesBase.GPSTIMECOND || checkTimeCnd(configImpl))
						result = createTracking(code);
				}
			} catch(Exception e){
				e.printStackTrace();
			}
			finally{
				configImpl.close();
			}
		}
		
		return result;
	}
	
	private static boolean checkTimeCnd(ConfigImpl configImpl) {
		boolean result = true;
		configImpl.getData().key = KEY_GPSPERIOD;
		
		if(configImpl.read()){
			String val = configImpl.getData().value;
			if(val.trim().length() > 0){
				try{
					result = false;
					
					String[] cond = val.split(";");
					Calendar calendar = Calendar.getInstance();
					long now = calendar.getTimeInMillis();
					int d = calendar.get(Calendar.DAY_OF_WEEK) - 1;
					
					if(d == 0)
						d = 7;
					
					if(cond[0].contains(Integer.toString(d))){
						String[] beginStr = cond[1].split(":");
						calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(beginStr[0]));
						calendar.set(Calendar.MINUTE, Integer.parseInt(beginStr[1]));
						calendar.set(Calendar.SECOND, 0);
						long begin = calendar.getTimeInMillis();
						String[] endStr = cond[2].split(":");
						calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endStr[0]));
						calendar.set(Calendar.MINUTE, Integer.parseInt(endStr[1]));
						calendar.set(Calendar.SECOND, 59);
						long end = calendar.getTimeInMillis();
						
						result = now >= begin && now <= end;
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		
		return result;
	}

	private static TrackingOld createTracking(String code_id){
		if (isCodeForTracking(code_id))
			return tracking;
		else
			return null;
	}
	
	private static boolean isCodeForTracking(String code){
		return code.equals(GSM_ID) ||
			code.equals(GPS_POINT_ID) ||
			code.equals(GPS_ROUTE_ID);
	}
	
}
