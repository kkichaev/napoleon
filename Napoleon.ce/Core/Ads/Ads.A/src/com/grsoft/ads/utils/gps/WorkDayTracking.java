package com.grsoft.ads.utils.gps;

import android.content.Intent;
import android.location.Location;

import com.grsoft.ads.AdsService;
import com.grsoft.ads.dataobjects.WorkDay;
import com.grsoft.ads.dataobjects.impl.WorkDayImpl;
import com.grsoft.database.DbWriter;
import com.grsoft.util.GlobalServiceContext;
import com.grsoft.util.Util;
import com.grsoft.util.gps.GPSUtilNew;

public class WorkDayTracking {
	private static WorkDayTracking instance;
	private static Location lastLocation = null;
	public static final String ON_LOCATION_CHANGE_ACTION = 
			"com.grsoft.ads.utils.gps.WorkDayTracking.LOCATION_CHANGE_ACTION";
	public static final String DISTANCE = "distance";
	private static int i = 0;
	
	public static WorkDayTracking instance(){
		DbWriter.checkDBTable(WorkDay.class);
		
		if (instance == null)
			instance = new WorkDayTracking();
		
		return instance;
	}
	
	public static class WorkDayLocationListener extends GPSUtilNew.TheLocationListener{
		protected boolean isCalcDistance(){
			return true;
		}
		
		@Override
		public void onLocationChanged(Location location) {
			synchronized (this) {
				super.onLocationChanged(location);
				
				if(isCalcDistance()){
					if (lastLocation != null){
						WorkDayImpl workDayImpl = new WorkDayImpl();
						WorkDay workDay = workDayImpl.getData();
						workDay.date = Util.getDate();
						workDayImpl.read();
						workDay.distance += (int)lastLocation.distanceTo(location);
						workDay.params = 0;
						workDayImpl.write();
						workDayImpl.close();
						
						/*
						Toast.makeText(GlobalServiceContext.service, "дистанция: " 
						+ Integer.toString(workDayImpl.getData().distance), Toast.LENGTH_LONG).show();
						*/
						
						Intent intent = new Intent(ON_LOCATION_CHANGE_ACTION);
						intent.putExtra(DISTANCE, workDay.distance);
						intent.putExtra("test", i++);
						GlobalServiceContext.service.sendBroadcast(intent);
					}
					
					lastLocation = new Location(location);
				}
			}
		}
	}
	
	/***
	 * Возвращает текущую пройденную дистанцию
	 */
	public static int startWorking(){
		WorkDayImpl.closePrevDay();
		WorkDayImpl workDayImpl = new WorkDayImpl();
		workDayImpl.getData().date = Util.getDate();
		workDayImpl.read();

		if (workDayImpl.getData().begin == null || 
				workDayImpl.getData().begin.getTime() == 0)
			workDayImpl.getData().begin = Util.getDateTime();
		
		workDayImpl.getData().params = 0;
		workDayImpl.startWork(); 
		workDayImpl.write();
		workDayImpl.close();
		
		((AdsService)GlobalServiceContext.service).gpsInit();
		
		return workDayImpl.getData().distance;
	}
	
	public static void  endWorking() {
		WorkDayImpl workDayImpl = new WorkDayImpl();
		workDayImpl.getData().date = Util.getDate();
		
		if (workDayImpl.read()){
			workDayImpl.getData().end = Util.getDateTime();
			workDayImpl.getData().params = 0;
			workDayImpl.endWork();
			workDayImpl.write();
		}
		
		workDayImpl.close();
		
		GPSUtilNew.stop(((AdsService)GlobalServiceContext.service));
	}
	
	public static boolean isCanStart(){
		WorkDayImpl workDayImpl = new WorkDayImpl();
		workDayImpl.getData().date = Util.getDate();
		
		boolean result = false;
		
		if (workDayImpl.read())
			result = !workDayImpl.isWorkTimeActive();
		else
			result = true;
		
		workDayImpl.close();
		return result;
	}
	
	public static boolean isCanEnd(){
		return !isCanStart();
	}
	
	public static boolean isWorkingTime(){
		return isCanEnd();
	}
	
	public static int getDistance(){
		int result = 0;
		
		WorkDayImpl workDayImpl = new WorkDayImpl();
		workDayImpl.getData().date = Util.getDate();
		
		if (workDayImpl.read())
			result = workDayImpl.getData().distance;
		
		workDayImpl.close();
		
		return result;
	}
}
