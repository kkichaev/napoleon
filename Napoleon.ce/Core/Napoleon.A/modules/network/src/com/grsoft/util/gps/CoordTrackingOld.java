package com.grsoft.util.gps;

import android.location.Location;

import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.GPSPos;
import com.grsoft.dataobjects.GPSPosImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

/**
 * Базовый обработчик снятия координат 
 * @author kki
 *
 */
public class CoordTrackingOld implements TrackingOld {
	
	public CoordTrackingOld() {
		DbWriter.checkDBTable(GPSPos.class);
	}

	@Override
	public void onLocationChanged(Location location) {
		GPSPosImpl gpsPos = new GPSPosImpl();
		gpsPos.setCloseAfterWrite(true);
		GPSPos route = gpsPos.getData();
		route.latitude = (int)(location.getLatitude() * Consts.GPS_SCALE);
		route.longitude = (int)(location.getLongitude() * Consts.GPS_SCALE);
		route.speed = (int)(location.getSpeed() * 100);
		route.date = Util.getDateTime();
		route.isGSM = GPSUtilOld.isGSMProvider(location) ? 1 : 0;
		gpsPos.write();
	}

}
