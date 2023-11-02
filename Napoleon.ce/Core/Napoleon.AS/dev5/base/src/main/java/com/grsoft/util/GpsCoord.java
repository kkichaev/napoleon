/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * kki   07/05/2011   creating
 */
package com.grsoft.util;
import com.grsoft.aceteam.R;

import android.location.Location;

/***
 * Координаты объекта
 * @author kki
 *
 */
public class GpsCoord {
	public int latitude;
	public int longitude;
	public long time = 0;
	
	public GpsCoord(int lat, int lon, long time){
		this.latitude = lat;
		this.longitude = lon;
		this.time = time;
	}
	
	public GpsCoord(Location loc) {
		if( loc == null ) {
			latitude = 0;
			longitude = 0;
			time = 0;
		} else {
			latitude = (int)(loc.getLatitude() * Consts.GPS_SCALE); 
			longitude = (int)(loc.getLongitude() * Consts.GPS_SCALE);
			time = loc.getTime();
		}
	}
	
	public static GpsCoord empty = new GpsCoord(0,0,0);
}
