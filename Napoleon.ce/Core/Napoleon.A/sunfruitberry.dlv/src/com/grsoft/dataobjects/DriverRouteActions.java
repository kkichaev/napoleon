package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;
import com.grsoft.util.gps.GPSUtilNew;

@TableInfo(name="DriverRouteActions", keyFields="created", indexes="routeItemId")
@ServerInfo(name="DriverRouteActions")
public class DriverRouteActions extends DataObject {
	
	public static final int STAUS_ACTIVE = 0;
	public static final int STAUS_FINISHED = 1;
	public static final int STAUS_CANCEL = 2;
	public static final int STAUS_REJECT = 3;

	public static final int STAUS_DRIVER_FREE = 4;
	public static final int STAUS_DRIVER_WORKING = 5;
	public static final int STAUS_DRIVER_DRIVING = 6;
	public static final int STAUS_DRIVER_BROKE = 7;

	public static final int STAUS_IN_ROUTE = 8;
	public static final int STAUS_DONE_WIITH_RETURNS = 9;
	
	public Date created = new Date();
	
	public String routeItemId = "";
	public int params = 0;
	
	@Scale(value=Consts.GPS_SCALE)
	public int latitude = 0;

	@Scale(value=Consts.GPS_SCALE)
	public int longitude = 0;
	
	public Date stltime = new Date();
	
	public int status = 0;
	
	public String docNumber = "";
	
	public boolean isEmpty() { return routeItemId.length() == 0; }
	
	public static DriverRouteActions getActiveItem() {
		DriverRouteActions ret = new DriverRouteActions();
		
		String where = "routeItemId <> '' and (status=" + Integer.toString(STAUS_ACTIVE) + " or status=" + Integer.toString(STAUS_CANCEL) + ")";
		DbReader r = new DbReader();
		r.select(ret,ret.getTableName(), where, "created desc");
		r.close();
		
		if(ret.status != STAUS_ACTIVE)
			ret = new DriverRouteActions();
		
		return ret;
	}
	
	public static String getActiveItemId() {
		return getActiveItem().routeItemId;
	}
	
	public static DriverRouteActions setStatus(String routeItemId, String docNumber, int status, String comment) {
		//
		// DriverRouteActions.STAUS_FINISHED- doc 
		// DriverRouteActions.STAUS_REJECT - doc
		// DriverRouteActions.STAUS_CANCEL - route
		// DriverRouteActions.STAUS_ACTIVE - route
		//
				
		DriverRouteActions data = new DriverRouteActions();
		data.routeItemId = routeItemId;
		data.params = 0;
		data.status = status;
		data.docNumber = docNumber;
		
		GpsCoord coord = GPSUtilNew.getLastKnownLocation();
		if(coord != null) {
			data.latitude = coord.latitude;
			data.longitude = coord.longitude;
			data.stltime = new Date(coord.time);
		}

		DbWriter.checkDBTable(data.getClass());
		
		long delDate = Util.getDate().getTime() - 3 * 24 * 3600 * 1000;
		String sql = "DELETE FROM " + data.getTableName() + " where ((params & " + Integer.toString(ParamState.ofExported) + ") <> 0) and created < " + 
				Long.toString(delDate);
		DataBaseManager.getDataBase().execSQL(sql);
		DbWriter wr = new DbWriter();
		wr.insertRecord(data);
		wr.close();
		return data;
	}
}
