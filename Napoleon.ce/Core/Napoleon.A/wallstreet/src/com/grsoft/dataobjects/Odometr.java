package com.grsoft.dataobjects;

import java.util.Date;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;


@TableInfo(name="odometr", keyFields="start")
public class Odometr extends DataObject {
	public Date start;
	@Scale(value=Consts.GPS_SCALE)
	public int start_lat;
	@Scale(value=Consts.GPS_SCALE)
	public int start_long;
	public int start_odo;
	public int start_rest;
	
	public Date end;
	@Scale(value=Consts.GPS_SCALE)
	public int end_lat;
	@Scale(value=Consts.GPS_SCALE)
	public int end_long;
	public int end_odo;
	public int end_rest;
	
	public int params;
	
	public int refuel;

}
